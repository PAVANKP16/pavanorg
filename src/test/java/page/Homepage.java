package page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Homepage {

	@FindBy(xpath = "//span[.='Contact Us']")
	private WebElement contactUs;
	
	public Homepage(WebDriver driver)
	{
		PageFactory.initElements(driver, this);
	}
	
	public String getLabel()
	{
		return contactUs.getText();
	}
}
