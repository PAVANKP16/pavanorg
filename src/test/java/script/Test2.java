package script;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import generic.BaseTest;
import generic.Utility;
import page.Homepage;

public class Test2 extends BaseTest {
	
	
	@Test(priority = 2)
	public void testB()
	
	{
		String v = Utility.getXldata(XLPATH, "test1", 0, 0);
		test.info("From xl: "+v);
		
		
		String title = driver.getTitle();
		Reporter.log(title,true);
		
		Homepage homepage = new Homepage(driver);
		String label = homepage.getLabel();
		Reporter.log(label,true);
		Assert.assertEquals(label, "CONTACT ME");
	}
}
