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

package com.mytest;

import org.apache.xmlbeans.impl.marshal.util.ArrayUtils;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

public class YourClass
{
    private YourClass myBoss = null;

    private MyClass myClass;
    private MySubClass mySubClass = new MySubClass();

    private float myFloat;
    private float attrib;
    private boolean someBool;
//    private List bools;// = newBoolList();
//    private List strs;// = newStringList();
    private long[] longArray;// = {RND.nextLong(), RND.nextLong()};

    private boolean[] booleanArray;// = {true, false, true};
    private String[] stringArray = {"ONE:"+RND.nextInt(), "TWO:"+RND.nextInt()};
    private MyClass[] myClassArray;//{new MyClass(), new MyClass()};

    private QName qn = new QName("URI" + RND.nextInt(), "LNAME"+RND.nextInt());
    private QName qn2 = new QName("URI" + RND.nextInt(), "LNAME"+RND.nextInt());


    private String[] wrappedArrayOne = {"W1", "W2"};
    private String[][] wrappedArrayTwo = {wrappedArrayOne, null, wrappedArrayOne};


    //hack alert
    static final Random RND = new Random();

    private List newStringList()
    {
        ArrayList l = new ArrayList();
        l.add("one:" + RND.nextInt());
        l.add("two:" + RND.nextInt());
        l.add(null);
        l.add("three:" + RND.nextInt());
        return l;
    }

    private List newBoolList()
    {
        ArrayList l = new ArrayList();
        l.add(Boolean.TRUE);
        l.add(Boolean.FALSE);
//        l.add(null);
//        l.add(Boolean.TRUE);
//        l.add(Boolean.FALSE);
        return l;
    }

    public float getMyFloat()
    {
        return myFloat;
    }

    public void setMyFloat(float myFloat)
    {
        this.myFloat = myFloat;
    }

    public YourClass getMyBoss()
    {
        return myBoss;
    }

    public void setMyBoss(YourClass myBoss)
    {
        this.myBoss = myBoss;
    }

    public MyClass getMyClass()
    {
        return myClass;
    }

    public void setMyClass(MyClass myClass)
    {
        this.myClass = myClass;
    }

    public boolean isSomeBool()
    {
        return someBool;
    }

    public void setSomeBool(boolean someBool)
    {
        this.someBool = someBool;
    }
//
//    public List getBools()
//    {
//        return bools;
//    }
//
//    public void setBools(List bools)
//    {
//        this.bools = bools;
//    }


    /**
     *  @xsdgen:attribute.name Attrib
     */
    public float getAttrib()
    {
        return attrib;
    }

    public void setAttrib(float attrib)
    {
        this.attrib = attrib;
    }

//    public List getStrs()
//    {
//        return strs;
//    }
//
//    public void setStrs(List strs)
//    {
//        this.strs = strs;
//    }

    public long[] getLongArray()
    {
        return longArray;
    }

    public void setLongArray(long[] longArray)
    {
        this.longArray = longArray;
    }


    public String[] getStringArray()
    {
        return stringArray;
    }

    public void setStringArray(String[] stringArray)
    {
        this.stringArray = stringArray;
    }

    public MyClass[] getMyClassArray()
    {
        return myClassArray;
    }

    public void setMyClassArray(MyClass[] myClassArray)
    {
        this.myClassArray = myClassArray;
    }

    public boolean[] getBooleanArray()
    {
        return booleanArray;
    }

    public void setBooleanArray(boolean[] booleanArray)
    {
        this.booleanArray = booleanArray;
    }


    public MySubClass getMySubClass()
    {
        return mySubClass;
    }

    public void setMySubClass(MySubClass mySubClass)
    {
        this.mySubClass = mySubClass;
    }

    public QName getQn()
    {
        return qn;
    }

    public void setQn(QName qn)
    {
        this.qn = qn;
    }

    public QName getQn2()
    {
        return qn2;
    }

    public void setQn2(QName qn2)
    {
        this.qn2 = qn2;
    }

    public String[] getWrappedArrayOne()
    {
        return wrappedArrayOne;
    }

    public void setWrappedArrayOne(String[] wrappedArrayOne)
    {
        this.wrappedArrayOne = wrappedArrayOne;
    }

    public String[][] getWrappedArrayTwo()
    {
        return wrappedArrayTwo;
    }

    public void setWrappedArrayTwo(String[][] wrappedArrayTwo)
    {
        this.wrappedArrayTwo = wrappedArrayTwo;
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof YourClass)) return false;

        final YourClass yourClass = (YourClass)o;

        if (attrib != yourClass.attrib) return false;
        if (myFloat != yourClass.myFloat) return false;
        if (someBool != yourClass.someBool) return false;
        if (!Arrays.equals(booleanArray, yourClass.booleanArray)) return false;
//        if (bools != null ? !bools.equals(yourClass.bools) : yourClass.bools != null) return false;
        if (!Arrays.equals(longArray, yourClass.longArray)) return false;
        if (myClass != null ? !myClass.equals(yourClass.myClass) : yourClass.myClass != null) return false;
        if (!Arrays.equals(myClassArray, yourClass.myClassArray)) return false;
        if (!Arrays.equals(stringArray, yourClass.stringArray)) return false;
        if (!Arrays.equals(wrappedArrayOne, yourClass.wrappedArrayOne)) return false;
//        if (strs != null ? !strs.equals(yourClass.strs) : yourClass.strs != null) return false;

        if (qn != null ? !qn.equals(yourClass.qn) : yourClass.qn != null) return false;
        if (qn2 != null ? !qn2.equals(yourClass.qn2) : yourClass.qn2 != null) return false;


        return true;
    }

    public int hashCode()
    {
        int result;
        result = (myClass != null ? myClass.hashCode() : 0);
        result = 29 * result + Float.floatToIntBits(myFloat);
        result = 29 * result + Float.floatToIntBits(attrib);
        result = 29 * result + (someBool ? 1 : 0);
//        result = 29 * result + (bools != null ? bools.hashCode() : 0);
//        result = 29 * result + (strs != null ? strs.hashCode() : 0);
        return result;
    }




    public String toString()
    {
        return "com.mytest.YourClass{" +
            "myClass=" + myClass +
            ", myFloat=" + myFloat +
            ", attrib=" + attrib +
            ", someBool=" + someBool +
            ", qn=" + qn +
            ", qn2=" + qn2 +
//            ", bools=" + (bools == null ? null : "size:" + bools.size() + bools) +
//            ", strs=" + (strs == null ? null : "size:" + strs.size() + strs) +
            ", longArray=" + ArrayUtils.arrayToString(longArray) +
            ", booleanArray=" + ArrayUtils.arrayToString(booleanArray) +
            ", stringArray=" + ArrayUtils.arrayToString(stringArray) +
            ", wrappedArrayOne=" + ArrayUtils.arrayToString(wrappedArrayOne) +
            ", myClassArray=" + ArrayUtils.arrayToString(myClassArray) +
            "}";
    }



}
