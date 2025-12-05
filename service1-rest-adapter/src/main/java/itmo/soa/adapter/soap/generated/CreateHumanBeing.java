
package itmo.soa.adapter.soap.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createHumanBeing complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createHumanBeing"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="humanBeing" type="{http://soap.service1.soa.itmo/}humanBeingDTO" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createHumanBeing", propOrder = {
    "humanBeing"
})
public class CreateHumanBeing {

    protected HumanBeingDTO humanBeing;

    /**
     * Gets the value of the humanBeing property.
     * 
     * @return
     *     possible object is
     *     {@link HumanBeingDTO }
     *     
     */
    public HumanBeingDTO getHumanBeing() {
        return humanBeing;
    }

    /**
     * Sets the value of the humanBeing property.
     * 
     * @param value
     *     allowed object is
     *     {@link HumanBeingDTO }
     *     
     */
    public void setHumanBeing(HumanBeingDTO value) {
        this.humanBeing = value;
    }

}
