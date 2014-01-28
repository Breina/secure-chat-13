package alpha.webservices.jaxbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginBean
{
	@XmlElement public String username;
	@XmlElement public String password; 
}