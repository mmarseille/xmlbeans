/*   Copyright 2004 The Apache Software Foundation
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*  limitations under the License.
*/
package org.apache.xmlbeans.test.performance.v1;

import java.io.CharArrayReader;

import org.apache.xmlbeans.test.performance.utils.PerfUtil;
import org.apache.xmlbeans.test.performance.utils.Constants;

// required by v2
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomWalkNLV1
{

  public static void main(String[] args) throws Exception
  {

    final int iterations = Constants.CURSOR_ITERATIONS;
    String flavor;

    if(args.length == 0)
      flavor = "deep-attributes";
    else
      flavor = args[0];

    DomWalkNLV1 test = new DomWalkNLV1();
    PerfUtil util = new PerfUtil();
    long cputime;
    int hash = 0;

    // get the xml cursor
    char[] chars = util.createXmlData(flavor, Constants.XML_SIZE);
    XmlObject doc = XmlObject.Factory.parse(new CharArrayReader(chars));
    Node node = doc.newDomNode();

    // warm up the vm
    cputime = System.currentTimeMillis();
    for(int i=0; i<iterations; i++){     
      hash += test.run(node);
    }
    cputime = System.currentTimeMillis() - cputime;

    // run it again for the real measurement
    cputime = System.currentTimeMillis();
    for(int i=0; i<iterations; i++){     
      hash += test.run(node);
    }
    cputime = System.currentTimeMillis() - cputime;
      
    // print the results
    System.out.print(Constants.DELIM+test.getClass().getSimpleName()+" flavor="+flavor+" ");
    System.out.print("hash "+hash+" ");
    System.out.print("time "+cputime+"\n");
  }

  private int run(Node p_node)
  {
    int result = 0;
    short type = p_node.getNodeType();

    if(type == Node.ATTRIBUTE_NODE ||
       type == Node.CDATA_SECTION_NODE ||
       type == Node.TEXT_NODE)
    {
      result = p_node.getNodeValue().length();
    }
    else
    {
      result = p_node.getNodeName().length();
    }

    NodeList children = p_node.getChildNodes();
    for(int i=0; i<children.getLength(); i++)
    {
      result += run(children.item(i));
    }

    return result;
  }
}