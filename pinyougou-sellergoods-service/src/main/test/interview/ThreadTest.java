package interview;

import com.pinyougou.pojo.TbBrand;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ThreadTest extends Thread {
    private int i;

    @Override
    public void run() {
        for (; i < 100; i++) {
            //打印当前线程的名字
            System.out.println(Thread.currentThread().getName() + " " + i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + " " + i);
            if (i == 20) {
                new ThreadTest().start();
                Thread.sleep(1);
                new ThreadTest().start();
            }
        }
    }

    @Test
    public void TestDemo1() {
        List<TbBrand> list = new ArrayList<>();
        TbBrand brand = new TbBrand();
        for (Long i = Long.valueOf(0); i < 3; i++) {
            brand.setId(i);
            list.add(brand);
        }
        for (TbBrand tbBrand : list) {
            System.out.println(tbBrand.toString());
        }
    }
}
