package alpha.webservices.jaxbeans;

import javax.xml.bind.annotation.XmlElement;

public class ChangePassBean
{
	@XmlElement public String password;
	@XmlElement public String username;
}
