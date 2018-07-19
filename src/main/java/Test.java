import com.xxx.rpc.HelloServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    private static HelloServiceImpl helloService;

    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("sp.xml");
        helloService = (HelloServiceImpl) context.getBean("hello");
        helloService.hello("w");
        ThreadLocal threadLocal = new ThreadLocal();

    }
}
