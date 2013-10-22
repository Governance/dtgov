package org.overlord.sramp.governance;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://overlord.jboss.com/v1")
public class ValueEntity
{
   @XmlAttribute
   private String value;

   public ValueEntity()
   {
   }

   public ValueEntity(String value)
   {
      this.value = value;
   }

   public String getValue()
   {
      return value;
   }
}