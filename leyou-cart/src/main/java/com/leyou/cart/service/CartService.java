package com.leyou.cart.service;

import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private  static final String KEY_PREFIX="user:cart";

    public void addCart(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        String key=cart.getSkuId().toString();
        Integer num= cart.getNum();

       if(hashOperations.hasKey(key)){
           String cartJson  = hashOperations.get(key).toString();
           cart= JsonUtils.parse(cartJson, Cart.class);
           cart.setNum(cart.getNum()+num);

       }else{
           Sku sku = this.goodsClient.querySkuBySkuId(cart.getSkuId());


           cart.setUserId(userInfo.getId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setImage(StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
            cart.setPrice(sku.getPrice());





       }
        hashOperations.put(key,JsonUtils.serialize(cart));
    }

    public List<Cart> queryCarts() {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        if(!this.redisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            return  null;
        }

        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());



        List<Object> cartsJson = hashOperations.values();


        if(CollectionUtils.isEmpty(cartsJson)){
            return  null;
        }

        return cartsJson.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(), Cart.class)).collect(Collectors.toList());

    }

    public void updateNum(Cart cart) {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        if(!this.redisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            return ;
        }


        Integer num= cart.getNum();

        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());


        String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();


        cart = JsonUtils.parse(cartJson, Cart.class);


        cart.setNum(num);

        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    public void deleteCart(Long skuId) {

        UserInfo user = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        hashOps.delete(skuId.toString());
    }
}
