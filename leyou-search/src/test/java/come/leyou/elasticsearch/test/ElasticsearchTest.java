package come.leyou.elasticsearch.test;


import com.leyou.LeyouSearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.ws.soap.Addressing;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = LeyouSearchApplication.class)
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsRepository goodsRepository;


    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsClient goodsClient;

    @Test
    public  void test(){
        this.elasticsearchTemplate.createIndex(Goods.class);
        this.elasticsearchTemplate.putMapping(Goods.class);


        Integer page=1;
        Integer rows=100;


        do{
            PageResult<SpuBo> result = this.goodsClient.querySpuByPage(null, null, page, rows);
          //  System.out.println("//////////////");

            List<SpuBo> items = result.getItems();


            List<Goods> goodsList = items.stream().map(spuBo -> {
                try {
//                    if(spuBo==null){
//                        System.out.println("//////////////////");
//                    }
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }

               // System.out.println("//////////////////////");
                return null;
            }).collect(Collectors.toList());

            this.goodsRepository.saveAll(goodsList);
            rows=items.size();
        //    System.out.println(rows);
            page++;
        }while(rows==100);

    }
}
