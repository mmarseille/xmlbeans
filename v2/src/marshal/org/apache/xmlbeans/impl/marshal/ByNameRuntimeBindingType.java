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

package org.apache.xmlbeans.impl.marshal;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlRuntimeException;
import org.apache.xmlbeans.impl.binding.bts.BindingLoader;
import org.apache.xmlbeans.impl.binding.bts.BindingProperty;
import org.apache.xmlbeans.impl.binding.bts.BindingType;
import org.apache.xmlbeans.impl.binding.bts.BindingTypeName;
import org.apache.xmlbeans.impl.binding.bts.ByNameBean;
import org.apache.xmlbeans.impl.binding.bts.JavaTypeName;
import org.apache.xmlbeans.impl.binding.bts.MethodName;
import org.apache.xmlbeans.impl.binding.bts.QNameProperty;
import org.apache.xmlbeans.impl.binding.bts.SimpleBindingType;
import org.apache.xmlbeans.impl.marshal.util.collections.Accumulator;
import org.apache.xmlbeans.impl.marshal.util.collections.AccumulatorFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;


final class ByNameRuntimeBindingType
    implements RuntimeBindingType
{
    private final ByNameBean byNameBean;
    private final Class javaClass;
    private final Property[] attributeProperties;
    private final Property[] elementProperties;
    private final boolean hasMulti;  //has any multi properties
    private final boolean hasDefaultAttributes;  //has any attributes with defaults

    //is this a subtype of something besides the ultimate parent type?
    //(XmlObject or java.lang.Object, though only the latter
    //is currently considered)
    private final boolean isSubType;


    //DO NOT CALL THIS CONSTRUCTOR, use the RuntimeTypeFactory
    ByNameRuntimeBindingType(ByNameBean btype)
    {
        byNameBean = btype;
        try {
            javaClass = getJavaClass(btype, getClass().getClassLoader());
        }
        catch (ClassNotFoundException e) {
            final String msg = "failed to load " + btype.getName().getJavaName();
            throw new XmlRuntimeException(msg, e);
        }

        int elem_prop_cnt = 0;
        int att_prop_cnt = 0;
        boolean has_multi = false;
        boolean has_attribute_defaults = false;
        final Collection type_props = btype.getProperties();
        for (Iterator itr = type_props.iterator(); itr.hasNext();) {
            QNameProperty p = (QNameProperty)itr.next();
            if (p.isMultiple()) has_multi = true;
            if (p.isAttribute()) {
                att_prop_cnt++;
                if (p.getDefault() != null) {
                    has_attribute_defaults = true;
                }
            } else {
                elem_prop_cnt++;
            }
        }

        attributeProperties = new Property[att_prop_cnt];
        elementProperties = new Property[elem_prop_cnt];
        hasMulti = has_multi;
        hasDefaultAttributes = has_attribute_defaults;

        isSubType = determineIsSubType(javaClass);
    }

    private static boolean determineIsSubType(Class javaClass)
    {
        int cnt = 0;
        for (Class p = javaClass.getSuperclass(); p != null; p = p.getSuperclass()) {
            if (cnt > 0) return true;
            cnt++;
        }
        return false;
    }

    //prepare internal data structures for use
    public void initialize(RuntimeBindingTypeTable typeTable,
                           BindingLoader loader)
    {
        int att_idx = 0;
        int elem_idx = 0;
        for (Iterator itr = byNameBean.getProperties().iterator(); itr.hasNext();) {
            QNameProperty bprop = (QNameProperty)itr.next();
            final boolean is_att = bprop.isAttribute();

            final Property prop = new Property(is_att ? att_idx : elem_idx,
                                               javaClass, hasMulti, bprop,
                                               typeTable, loader);
            if (is_att)
                attributeProperties[att_idx++] = prop;
            else
                elementProperties[elem_idx++] = prop;

        }
    }

    Object createIntermediary(UnmarshalResult context)
    {
        if (hasMulti) {
            return new UResultHolder(this);
        } else {
            return ClassLoadingUtils.newInstance(javaClass);
        }
    }

    Object getFinalObjectFromIntermediary(Object retval,
                                          UnmarshalResult context)
    {
        if (hasMulti) {
            UResultHolder rh = (UResultHolder)retval;
            return rh.getFinalValue();
        } else {
            return retval;
        }
    }

    private static Class getJavaClass(BindingType btype, ClassLoader backup)
        throws ClassNotFoundException
    {
        final JavaTypeName javaName = btype.getName().getJavaName();
        String jclass = javaName.toString();
        return ClassLoadingUtils.loadClass(jclass, backup);
    }


    RuntimeBindingProperty getElementProperty(int index)
    {
        return elementProperties[index];
    }

    RuntimeBindingProperty getAttributeProperty(int index)
    {
        return attributeProperties[index];
    }

    //TODO: optimize this linear scan
    RuntimeBindingProperty getMatchingElementProperty(String uri,
                                                      String localname)
    {
        for (int i = 0, len = elementProperties.length; i < len; i++) {
            final Property prop = elementProperties[i];

            if (doesPropMatch(uri, localname, prop))
                return prop;
        }
        return null;
    }

    //TODO: optimize this linear scan
    RuntimeBindingProperty getMatchingAttributeProperty(String uri,
                                                        String localname,
                                                        UnmarshalResult context)
    {
        for (int i = 0, len = attributeProperties.length; i < len; i++) {
            final Property prop = attributeProperties[i];

            if (doesPropMatch(uri, localname, prop)) {
                if (hasDefaultAttributes && (prop.defaultValue != null)) {
                    context.attributePresent(i);
                }
                return prop;
            }
        }
        return null;
    }

    private static boolean doesPropMatch(String uri,
                                         String localname,
                                         Property prop)
    {
        assert localname != null;

        final QName qn = prop.getQName();

        if (qn.getLocalPart().equals(localname)) {
            //QNames always uses "" for no namespace, but the incoming uri
            //might use null or "".
            return qn.getNamespaceURI().equals(uri == null ? "" : uri);
        }
        return false;
    }

    public int getElementPropertyCount()
    {
        return elementProperties.length;
    }

    public int getAttributePropertyCount()
    {
        return attributeProperties.length;
    }

    public boolean isSubType()
    {
        return isSubType;
    }

    public boolean hasDefaultAttributes()
    {
        return hasDefaultAttributes;
    }

    public QName getSchemaTypeName()
    {
        return byNameBean.getName().getXmlName().getQName();
    }

    public void fillDefaultAttributes(Object inter, UnmarshalResult context)
    {
        if (!hasDefaultAttributes) return;

        for (int aidx = 0, alen = attributeProperties.length; aidx < alen; aidx++) {
            final Property p = attributeProperties[aidx];

            if (p.defaultValue == null) continue;
            if (context.isAttributePresent(aidx)) continue;

            p.fillDefaultValue(inter);
        }
    }


    private static final class Property implements RuntimeBindingProperty
    {
        private final int propertyIndex;
        private final Class beanClass;
        private final boolean beanHasMulti;          //consider a subclass
        private final QNameProperty bindingProperty;
        private final BindingType bindingType;
        private final Class propertyClass;
        private final Class collectionElementClass; //null for non collections
        private final TypeUnmarshaller unmarshaller;
        private final TypeMarshaller marshaller; // used only for simple types
        private final Method getMethod;
        private final Method setMethod;
        private final boolean javaPrimitive;
        private final Object defaultValue;

        private static final Object[] EMPTY_OBJECT_ARRAY = new Object[]{};

        Property(int property_index,
                 Class beanClass,
                 boolean bean_has_multis,
                 QNameProperty prop,
                 RuntimeBindingTypeTable typeTable,
                 BindingLoader loader)
        {
            if (prop.getQName() == null) {
                final String msg = "property " + property_index + " of " +
                    beanClass + " has no qname";
                throw new IllegalArgumentException(msg);
            }

            this.propertyIndex = property_index;
            this.beanClass = beanClass;
            this.beanHasMulti = bean_has_multis;
            this.bindingProperty = prop;
            this.unmarshaller = lookupUnmarshaller(prop, typeTable, loader);
            this.marshaller = lookupMarshaller(prop, typeTable, loader);
            this.bindingType = loader.getBindingType(prop.getTypeName());
            propertyClass = getPropertyClass(prop, bindingType);
            collectionElementClass = getCollectionElementClass(prop, bindingType);
            getMethod = getGetterMethod(prop, beanClass);
            setMethod = getSetterMethod(prop, beanClass);
            javaPrimitive = propertyClass.isPrimitive();

            String def = bindingProperty.getDefault();
            if (def != null) {
                defaultValue = extractDefaultObject(def, bindingType,
                                                    typeTable, loader);
                if (!prop.isAttribute()) {
                    //TODO: deal with defaulting elements!
                    System.out.println("Default elements not supported: " + this);
                }
            } else {
                defaultValue = null;
            }
        }


        //REVIEW: find a shorter path to our goal.
        private static Object extractDefaultObject(String value,
                                                   BindingType bindingType,
                                                   RuntimeBindingTypeTable typeTable,
                                                   BindingLoader loader)
        {
            final String xmldoc = "<a>" + value + "</a>";
            try {
                final UnmarshallerImpl um = new UnmarshallerImpl(loader, typeTable);
                final StringReader sr = new StringReader(xmldoc);
                final XMLStreamReader reader =
                    um.getXmlInputFactory().createXMLStreamReader(sr);
                final BindingTypeName btname = bindingType.getName();
                final Object obj =
                    um.unmarshalType(reader, btname.getXmlName().getQName(),
                                     btname.getJavaName().toString());
                reader.close();
                sr.close();
                return obj;
            }
            catch (XmlException e) {
                throw new XmlRuntimeException(e);
            }
            catch (XMLStreamException e) {
                throw new XmlRuntimeException(e);
            }
        }

        private Class getPropertyClass(QNameProperty prop, BindingType btype)
        {
            assert btype != null;

            final Class propertyClass;
            try {
                final ClassLoader our_cl = getClass().getClassLoader();
                final JavaTypeName collectionClass = prop.getCollectionClass();

                if (collectionClass == null) {
                    propertyClass = getJavaClass(btype, our_cl);
                } else {
                    final String col = collectionClass.toString();
                    propertyClass = ClassLoadingUtils.loadClass(col, our_cl);
                }
            }
            catch (ClassNotFoundException ex) {
                final String s = "error loading " +
                    btype.getName().getJavaName();
                throw (RuntimeException)(new RuntimeException(s).initCause(ex));
            }
            return propertyClass;
        }


        private Class getCollectionElementClass(QNameProperty prop,
                                                BindingType btype)
        {
            assert btype != null;

            try {
                final JavaTypeName collectionClass = prop.getCollectionClass();

                if (collectionClass == null) {
                    return null;
                } else {
                    final ClassLoader our_cl = getClass().getClassLoader();
                    return getJavaClass(btype, our_cl);
                }
            }
            catch (ClassNotFoundException ex) {
                final String s = "error loading " +
                    btype.getName().getJavaName();
                throw (RuntimeException)(new RuntimeException(s).initCause(ex));
            }
        }


        public BindingType getType()
        {
            return bindingType;
        }

        public QName getName()
        {
            return bindingProperty.getQName();
        }

        private TypeUnmarshaller lookupUnmarshaller(BindingProperty prop,
                                                    RuntimeBindingTypeTable table,
                                                    BindingLoader loader)
        {
            assert prop != null;
            final BindingTypeName type_name = prop.getTypeName();
            assert type_name != null;
            final BindingType binding_type = loader.getBindingType(type_name);
            if (binding_type == null) {
                throw new XmlRuntimeException("failed to load type: " +
                                              type_name);
            }

            TypeUnmarshaller um =
                table.getOrCreateTypeUnmarshaller(binding_type, loader);
            if (um == null) {
                throw new AssertionError("failed to get unmarshaller for " +
                                         type_name);
            }
            return um;
        }

        private TypeMarshaller lookupMarshaller(BindingProperty prop,
                                                RuntimeBindingTypeTable typeTable,
                                                BindingLoader loader)
        {
            final BindingType bindingType =
                loader.getBindingType(prop.getTypeName());
            TypeMarshaller m = typeTable.getTypeMarshaller(bindingType);

            if (m == null) {
                //TODO: FIXME for nested as-if types
                if (bindingType instanceof SimpleBindingType) {
                    SimpleBindingType stype = (SimpleBindingType)bindingType;

                    //let's try using the as if type
                    final BindingTypeName asif_name = stype.getAsIfBindingTypeName();
                    assert asif_name != null : "no asif for " + stype;
                    BindingType asif = loader.getBindingType(asif_name);
                    if (asif == null) {
                        throw new AssertionError("unable to get asif type" +
                                                 " for " + asif_name);
                    }
                    m = typeTable.getTypeMarshaller(asif);

                    if (m == null) {
                        final String msg = "asif type marshaller not found" +
                            " for" + stype + " asif=" + asif;
                        throw new AssertionError(msg);
                    }
                }
            }

            return m;
        }


        public TypeUnmarshaller getTypeUnmarshaller(UnmarshalResult context)
        {
            //don't need any xsi stuff for attributes.
            if (bindingProperty.isAttribute()) return unmarshaller;

            final QName xsi_type = context.getXsiType();

            if (xsi_type != null) {
                TypeUnmarshaller typed_um = context.getTypeUnmarshaller(xsi_type);
                if (typed_um != null)
                    return typed_um;
                //reaching here means some problem with extracting the
                //marshaller for the xsi type, so just use the expected one
            }

            if (context.hasXsiNil())
                return NullUnmarshaller.getInstance();

            return unmarshaller;
        }

        public void fill(final Object inter, final Object prop_obj)
        {
            //means xsi:nil was true but we're a primtive.
            //schema should have nillable="false" so this
            //is a validation problem
            if (prop_obj == null && javaPrimitive)
                return;

            try {
                if (beanHasMulti) {
                    final UResultHolder rh = (UResultHolder)inter;

                    if (isMultiple()) {
                        rh.addItem(propertyIndex, prop_obj);
                    } else {
                        setMethod.invoke(rh.getValue(), new Object[]{prop_obj});
                    }
                } else {
                    setMethod.invoke(inter, new Object[]{prop_obj});
                }
            }
            catch (SecurityException e) {
                throw new XmlRuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new XmlRuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new XmlRuntimeException(e);
            }
        }


        public void fillDefaultValue(Object inter)
        {
            assert (defaultValue != null);

            this.fill(inter, defaultValue);
        }

        public void fillCollection(final Object inter, final Object prop_obj)
        {
            assert isMultiple();
            try {
                setMethod.invoke(inter, new Object[]{prop_obj});
            }
            catch (SecurityException e) {
                throw new XmlRuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new XmlRuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new XmlRuntimeException(e);
            }
        }

        public CharSequence getLexical(Object value, MarshalResult result)
        {
            assert value != null :
                "null value for " + bindingProperty + " class=" + beanClass;

            assert  result != null :
                "null value for " + bindingProperty + " class=" + beanClass;

            assert marshaller != null :
                "null marshaller for prop=" + bindingProperty + " class=" +
                beanClass + " propType=" + bindingProperty.getTypeName();

            return marshaller.print(value, result);
        }

        public Object getValue(Object parentObject, MarshalResult result)
        {
            assert parentObject != null;
            assert beanClass.isAssignableFrom(parentObject.getClass()) :
                parentObject.getClass() + " is not a " + beanClass;
            try {
                return getMethod.invoke(parentObject, EMPTY_OBJECT_ARRAY);
            }
            catch (SecurityException e) {
                throw new XmlRuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new XmlRuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new XmlRuntimeException(e);
            }
        }

        //TODO: check isSet methods
        public boolean isSet(Object parentObject, MarshalResult result)
        {
            if (bindingProperty.isNillable())
                return true;

            Object val = getValue(parentObject, result);
            return (val != null);
        }

        public boolean isMultiple()
        {
            return bindingProperty.isMultiple();
        }

        public boolean isNillable()
        {
            return bindingProperty.isNillable();
        }

        private static Method getSetterMethod(QNameProperty binding_prop,
                                              Class beanClass)
        {
            MethodName setterName = binding_prop.getSetterName();
            try {
                final Method set_method = setterName.getMethodOn(beanClass);
                return set_method;
            }
            catch (NoSuchMethodException e) {
                throw new XmlRuntimeException(e);
            }
            catch (SecurityException e) {
                throw new XmlRuntimeException(e);
            }
            catch (ClassNotFoundException cnfe) {
                throw new XmlRuntimeException(cnfe);
            }
        }


        private static Method getGetterMethod(QNameProperty binding_prop,
                                              Class beanClass)
        {
            MethodName getterName = binding_prop.getGetterName();
            try {
                final Method get_method =
                    getterName.getMethodOn(beanClass);
                return get_method;
            }
            catch (NoSuchMethodException e) {
                throw new XmlRuntimeException(e);
            }
            catch (SecurityException e) {
                throw new XmlRuntimeException(e);
            }
            catch (ClassNotFoundException cnfe) {
                throw new XmlRuntimeException(cnfe);//should never happen
            }
        }


        QName getQName()
        {
            return bindingProperty.getQName();
        }


    }


    private static final class UResultHolder
    {
        private final ByNameRuntimeBindingType runtimeBindingType;
        private final Object value;
        private Accumulator[] accumulators;

        UResultHolder(ByNameRuntimeBindingType type)
        {
            runtimeBindingType = type;
            value = ClassLoadingUtils.newInstance(type.javaClass);
        }


        Object getFinalValue()
        {
            if (accumulators != null) {
                final Property[] props = runtimeBindingType.elementProperties;
                for (int i = 0, len = accumulators.length; i < len; i++) {
                    final Accumulator accum = accumulators[i];
                    if (accum != null) {
                        final Property prop = props[i];
                        prop.fillCollection(value, accum.getFinalArray());
                    }
                }
            }
            return value;
        }

        void addItem(int elem_idx, Object value)
        {
            initAccumulator(elem_idx);
            accumulators[elem_idx].append(value);
        }

        private void initAccumulator(int elem_idx)
        {
            Accumulator[] accs = accumulators;
            if (accs == null) {
                accs = new Accumulator[runtimeBindingType.getElementPropertyCount()];
                accumulators = accs;
            }
            if (accs[elem_idx] == null) {
                final Property p = runtimeBindingType.elementProperties[elem_idx];
                accs[elem_idx] =
                    AccumulatorFactory.createAccumulator(p.propertyClass,
                                                         p.collectionElementClass);
            }
        }

        public Object getValue()
        {
            return value;
        }


    }


}
