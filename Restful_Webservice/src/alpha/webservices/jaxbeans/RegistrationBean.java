package alpha.webservices.jaxbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistrationBean
{
	@XmlElement public String username;
	@XmlElement public String password;
	@XmlElement public String email;
	@XmlElement public String gcm_registration_id;
}
