/*
* The Apache Software License, Version 1.1
*
*
* Copyright (c) 2003 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution,
*    if any, must include the following acknowledgment:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowledgment may appear in the software itself,
*    if and wherever such third-party acknowledgments normally appear.
*
* 4. The names "Apache" and "Apache Software Foundation" must
*    not be used to endorse or promote products derived from this
*    software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache
*    XMLBeans", nor may "Apache" appear in their name, without prior
*    written permission of the Apache Software Foundation.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation and was
* originally based on software copyright (c) 2000-2003 BEA Systems
* Inc., <http://www.bea.com/>. For more information on the Apache Software
* Foundation, please see <http://www.apache.org/>.
*/

package org.apache.xmlbeans;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

/**
 * An Unmarshaller is used to unmarshal xml documents into Java objects.
 * The object is not thread safe and should not be shared
 * amonst threads.  It can however be shared across different invocations of
 * Unmarshaller.unmarshalType() for a given document.
 */
public interface Unmarshaller
{

    /**
     *  unmarshall an entire xml document.
     *
     * PRECONDITIONS:
     * reader must be positioned at or before the root
     * start element of the document.
     *
     * POSTCONDITIONS:
     * reader will be positioned immediately after
     * the end element corresponding to the start element from the precondition
     *
     * @param reader
     * @return
     * @throws XmlException
     */
    Object unmarshal(XMLStreamReader reader)
        throws XmlException;

    /**
     *  unmarshall an entire xml document.
     *
     * PRECONDITIONS:
     * reader must be positioned at or before the root
     * start element of the document.
     *
     * POSTCONDITIONS:
     * reader will be positioned immediately after
     * the end element corresponding to the start element from the precondition
     *
     *
     * <p>Use the <em>options</em> parameter to specify the following:</p>
     *
     * <ul>
     * <li>A collection instance that should be used as an error listener during
     * compilation, as described in {@link XmlOptions#setErrorListener}.</li>
     * </ul>
     *
     *
     * @param reader
     * @return
     * @throws XmlException
     */
    Object unmarshal(XMLStreamReader reader, XmlOptions options)
        throws XmlException;


    /**
     * unmarshall an entire xml document.  The encoding to use is determined
     * according to the heuristic specified in the XML 1.0 recommendation.
     *
     * @param xmlDocument
     * @return
     * @throws org.apache.xmlbeans.XmlException
     */
    Object unmarshal(InputStream xmlDocument)
        throws XmlException;

    /**
     * unmarshall an entire xml document.  The encoding to use is determined
     * according to the heuristic specified in the XML 1.0 recommendation.
     *
     *
     * <p>Use the <em>options</em> parameter to specify the following:</p>
     *
     * <ul>
     * <li>A collection instance that should be used as an error listener during
     * compilation, as described in {@link XmlOptions#setErrorListener}.</li>
     * </ul>
     *
     * @param xmlDocument
     * @return
     * @throws org.apache.xmlbeans.XmlException
     */
    Object unmarshal(InputStream xmlDocument, XmlOptions options)
        throws XmlException;

    /**
     * unmarshal an xml instance of a given schema type
     *
     * No attention is paid to the actual tag on which the reader is positioned.
     * It is only the contents that matter
     * (including attributes on that start tag).
     *
     *
     * PRECONDITIONS:
     * reader.isStartElement() must return true
     *
     * POSTCONDITIONS:
     * reader will be positioned immediately after the end element
     * corresponding to the start element from the precondition
     *
     * @param schemaType
     * @param javaType
     * @return
     * @throws org.apache.xmlbeans.XmlException
     */
    Object unmarshalType(XMLStreamReader reader,
                         QName schemaType,
                         String javaType)
        throws XmlException;

    /**
     * unmarshal an xml instance of a given schema type
     *
     * No attention is paid to the actual tag on which the reader is positioned.
     * It is only the contents that matter
     * (including attributes on that start tag).
     *
     *
     * PRECONDITIONS:
     * reader.isStartElement() must return true
     *
     * POSTCONDITIONS:
     * reader will be positioned immediately after the end element
     * corresponding to the start element from the precondition
     *
     * <p>Use the <em>options</em> parameter to specify the following:</p>
     *
     * <ul>
     * <li>A collection instance that should be used as an error listener during
     * compilation, as described in {@link XmlOptions#setErrorListener}.</li>
     * </ul>
     *
     *
     * @param schemaType
     * @param javaType
     * @return
     * @throws org.apache.xmlbeans.XmlException
     */
    Object unmarshalType(XMLStreamReader reader,
                         QName schemaType,
                         String javaType,
                         XmlOptions options)
        throws XmlException;
}