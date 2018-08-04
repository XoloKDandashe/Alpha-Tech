import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.regex;

class RegTest
{
    TestUser user;

    user.setFullName("Phuti Setoaba");
    user.setJobTittle("Intern");
    user.setPassword("KKPP2301");
    user.setTelephone("0125556666");
    user.setCompanyName("EpiUse");
    user.setWorkTelephone("0121234567");
    user.setEmailAddress("phuti.setoaba@gmail.com");
    user.setMobileNumber("0794673938");
    user.setWorkAddress("1173 South St");

   /* @Test
    testEmail(){

    String email = user.getEmail();

    assertThat();
    }*/

    @Test
    testPassword(){
    String password = user.getPassword();

    assertThat("KKPP2301", password);
    }



    }