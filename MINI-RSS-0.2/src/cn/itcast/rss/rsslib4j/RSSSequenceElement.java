//$Id: RSSSequenceElement.java,v 1.5 2004/03/25 10:09:10 taganaka Exp $
package cn.itcast.rss.rsslib4j;

/**
 * RSSSequenceElement's definitions class.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSSequenceElement {
  /**
 * @uml.property  name="resource"
 */
private String resource;

  /**
 * Set the sequence element resource
 * @param res  resource
 * @uml.property  name="resource"
 */
  public void setResource(String res){
    resource = res;
  }

  /**
 * Get the resource
 * @return  the resource
 * @uml.property  name="resource"
 */
  public String getResource(){
    return resource;
  }

  /**
   * For debug
   * @return an informational string
   */
  public String toString(){
    String info = "ELEMENT RESOURCE: " + resource;
    return info;
  }

}