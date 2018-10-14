package solrTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@ContextConfiguration("classpath:spring/applicationContext-solr.xml")
@RunWith(SpringRunner.class)
public class SpringSolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void templateAdd() {
        TbItem object = new TbItem();
        for (long i = 0; i < 100; i++) {
            object.setId(i);
            //object.setBrand("华为" + i);
            object.setTitle("测试的title" + i);
            solrTemplate.saveBean(object);
        }
        solrTemplate.commit();
    }

    @Test
    public void templateQuery() {
        TbItem byId = solrTemplate.getById(1L, TbItem.class);
        System.out.println(byId);
    }

    @Test
    public void templateUpdate() {
        TbItem tbItem = new TbItem();
        for (long i = 0; i < 100; i++) {
            tbItem.setId(i);
            tbItem.setTitle("华为为为upadate" + i);
            solrTemplate.saveBean(tbItem);
        }
        solrTemplate.commit();
    }

    @Test
    public void testPageQuery() {
        Query query = new SimpleQuery("*:*");
        query.setOffset(20);//(page-1)*rows
        query.setRows(20);//rows
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println(tbItems.getTotalElements() + " : 总的记录数");
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
        }

        System.out.println("---------------------------");

        List<TbItem> content = tbItems.getContent();
        for (TbItem tbItem : content) {
            System.out.println(tbItem);
        }
    }

    @Test
    public void testQueryOptions(){
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_title").contains("2");
        criteria.and("item_title").contains("5");
        query.addCriteria(criteria);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
        }
    }

    @Test
    public void testDeleteQuery(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
