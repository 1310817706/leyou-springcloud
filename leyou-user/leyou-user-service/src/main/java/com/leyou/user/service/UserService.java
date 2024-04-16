package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX="user:verify:";
    public Boolean checkUser(String data, Integer type) {
        User record=new User();
        if(type==1){
            record.setUsername(data);
        }else if(type==2){
            record.setPhone(data);
        }else{
            return null;
        }

      return   this.userMapper.selectCount(record)==0;

    }

    public void sendVerifyCode(String phone) {
        if(StringUtils.isBlank(phone)){
            return ;
        }

        String code = NumberUtils.generateCode(6);

        Map<String,String> msg=new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        this.amqpTemplate.convertAndSend("leyou.sms.exchange","verifycode.sms",msg);


        this.redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);

    }

    public void register(User user, String code) {

        String redisCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());


        if(!StringUtils.equals(code,redisCode)){
            return;
        }

        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);


        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));

        user.setId(null);
        user.setCreated(new Date());

        this.userMapper.insertSelective(user);



    }

    public User queryUser(String username, String password) {

        User record=new User();
        record.setUsername(username);

        User user=this.userMapper.selectOne(record);

             if(user==null){
                 return null;
             }

         password = CodecUtils.md5Hex(password, user.getSalt());

             if(StringUtils.equals(password,user.getPassword())){
                 return user;
             }

             return null;

    }
}
