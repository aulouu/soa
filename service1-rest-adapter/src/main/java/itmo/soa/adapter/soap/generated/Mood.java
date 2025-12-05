
package itmo.soa.adapter.soap.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mood.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="mood"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SADNESS"/&gt;
 *     &lt;enumeration value="SORROW"/&gt;
 *     &lt;enumeration value="APATHY"/&gt;
 *     &lt;enumeration value="FRENZY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "mood")
@XmlEnum
public enum Mood {

    SADNESS,
    SORROW,
    APATHY,
    FRENZY;

    public String value() {
        return name();
    }

    public static Mood fromValue(String v) {
        return valueOf(v);
    }

}
