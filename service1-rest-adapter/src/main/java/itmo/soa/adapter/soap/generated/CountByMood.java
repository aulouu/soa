
package itmo.soa.adapter.soap.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for countByMood complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="countByMood"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="moodValue" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "countByMood", propOrder = {
    "moodValue"
})
public class CountByMood {

    protected int moodValue;

    /**
     * Gets the value of the moodValue property.
     * 
     */
    public int getMoodValue() {
        return moodValue;
    }

    /**
     * Sets the value of the moodValue property.
     * 
     */
    public void setMoodValue(int value) {
        this.moodValue = value;
    }

}
