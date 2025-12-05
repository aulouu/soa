
package itmo.soa.adapter.soap.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for weaponType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="weaponType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="HAMMER"/&gt;
 *     &lt;enumeration value="AXE"/&gt;
 *     &lt;enumeration value="PISTOL"/&gt;
 *     &lt;enumeration value="RIFLE"/&gt;
 *     &lt;enumeration value="KNIFE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "weaponType")
@XmlEnum
public enum WeaponType {

    HAMMER,
    AXE,
    PISTOL,
    RIFLE,
    KNIFE;

    public String value() {
        return name();
    }

    public static WeaponType fromValue(String v) {
        return valueOf(v);
    }

}
