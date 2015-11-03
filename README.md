What?
====

This JUnit extension enables remote execution of a JUnit test, supporting arbitrary JUnit runner.erver is not running.

Why?
====

Useful for running your integration tests within the context of your running application that cannot be easily embedded into your test JVM.
In such a situation, it is easier to inject the test into the running application JVM.

How?
====

- Add the artifact 'com.datastax:remote-junit-runner:0.1' to your project
- Annotate test class with `@RunWith(com.datastax.junit.remote.Remote.class)`. 
  The annotation can be placed on a superclass.
- If you need to specify the JUnit that is going to be used on the remote side, 
  add @Remote.RunWith annotation on the test class:
  
  Running remotely a test suite:
   
```
@RunWith(Remote.class)
@Remote.RunWith(Suite.class)
@Suite.SuiteClasses({RemoteSuite.Test1.class})
public class RemoteSuite
{

    public static class Test1
    {
        @Test
        public void test1()
        {
            System.out.println("test1");
        }
    }
}
```

  Running remotelly parametrized tests:
  
```
@RunWith(Remote.class)
@Remote.RunWith(JUnitParamsRunner.class)
public class RemoteParametrizedTest
{
    @Parameters({
            "1, 2, 3",
            "2, 3, 5"

    })
    @Test
    public void test(int a, int b, int c)
    {
        Assert.assertEquals(c, a + b);
    }
}
```

  Remote Spock tests:
  
```
@RunWith(Remote)
@Remote.RunWith(Sputnik)
class RemoteSpockSpec extends Specification
{

    def test() {
        expect:
        true
    }
}
```  

- Additionally, you can specify the location of the remote server via @Remote.Host annotation. (default: localhost:4567)
- Start the remote server:

```
java -cp remote-junit-runner.jar com.datastax.junit.remote.RemoteTestServer
```

or embed it into your application (check the JavaDoc for RemoteTestServer)