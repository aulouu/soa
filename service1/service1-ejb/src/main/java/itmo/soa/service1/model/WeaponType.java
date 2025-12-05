package itmo.soa.service1.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(namespace = "http://soap.service1.soa.itmo/")
@XmlEnum
public enum WeaponType {
    HAMMER,
    AXE,
    PISTOL,
    RIFLE,
    KNIFE
}
