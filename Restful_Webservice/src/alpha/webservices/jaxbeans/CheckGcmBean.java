package alpha.webservices.jaxbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CheckGcmBean
{
	@XmlElement public String username;
}