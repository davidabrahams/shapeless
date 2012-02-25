/*
 * Copyright (c) 2011 Miles Sabin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shapeless

import org.junit.Test
import org.junit.Assert._

class HListTests {
  import HList._
  import Typeable._
  import Poly._
  import Traversables._
  import Nat._
  import Tuples._

  type SI = Set[Int] :: HNil
  type OI = Option[Int] :: HNil

  type SISS = Set[Int] :: Set[String] :: HNil
  type OIOS = Option[Int] :: Option[String] :: HNil
    
  type ISII = Int :: String :: Int :: Int :: HNil
  type IIII = Int :: Int :: Int :: Int :: HNil
  type IYII = Int :: Any :: Int :: Int :: HNil

  type OIOSOIOI = Option[Int] :: Option[String] :: Option[Int] :: Option[Int] :: HNil
  type SISSSISI = Set[Int] :: Set[String] :: Set[Int] :: Set[Int] :: HNil

  type BBBB = Boolean :: Boolean :: Boolean :: Boolean :: HNil
  
  trait Fruit
  case class Apple() extends Fruit
  case class Pear() extends Fruit
  case class Banana() extends Fruit
  
  type YYYY = Any :: Any :: Any :: Any :: HNil
  type FF = Fruit :: Fruit :: HNil
  type AP = Apple :: Pear :: HNil
  type BP = Banana :: Pear :: HNil
  type AF = Apple :: Fruit :: HNil
  type FFFF = Fruit :: Fruit :: Fruit :: Fruit :: HNil
  type APAP = Apple :: Pear :: Apple :: Pear :: HNil
  type APBP = Apple :: Pear :: Banana :: Pear :: HNil
  type APB = Apple :: Pear :: Banana :: HNil
  type PBPA = Pear :: Banana :: Pear :: Apple :: HNil
  type PABP = Pear :: Apple :: Banana :: Pear :: HNil
  
  val a : Apple = Apple()
  val p : Pear = Pear()
  val b : Banana = Banana()
  val f : Fruit = new Fruit {}

  val ap : AP = a :: p :: HNil
  val bp : BP = b :: p :: HNil
  val apap : APAP = a :: p :: a :: p :: HNil
  val apbp : APBP = a :: p :: b :: p :: HNil

  def typed[T](t : => T) {}
  
  @Test
  def testBasics {
    val l = 1 :: "foo" :: 2.0 :: HNil
    
    typed[Int](l.head)
    assertEquals(1, l.head)
    
    typed[String](l.tail.head) 
    assertEquals("foo", l.tail.head)
    
    typed[Double](l.tail.tail.head)
    assertEquals(2.0, l.tail.tail.head, Double.MinPositiveValue)
  }
  
  @Test
  def testMap {
    implicitly[MapperAux[choose.type, HNil, HNil]]
    implicitly[Case[choose.type, Set[Int] => Option[Int]]]
    implicitly[choose.λ[Int]]
    implicitly[MapperAux[choose.type, Set[Int] :: HNil, Option[Int] :: HNil]]
    
    val s1 = Set(1) :: HNil
    val o1 = s1 map choose
    typed[OI](o1)
    assertEquals(Option(1) :: HNil, o1)

    val s2 = Set(1) :: Set("foo") :: HNil
    val o2 = s2 map choose
    typed[OIOS](o2)
    assertEquals(Option(1) :: Option("foo") :: HNil, o2)
    
    val l1 = 1 :: "foo" :: 2 :: 3 :: HNil

    val l2 = l1 map singleton
    typed[SISSSISI](l2)
    assertEquals(Set(1) :: Set("foo") :: Set(2) :: Set(3) :: HNil, l2)

    val l3 = l1 map option
    typed[OIOSOIOI](l3)
    assertEquals(Option(1) :: Option("foo") :: Option(2) :: Option(3) :: HNil, l3)

    val l4 = Option(1) :: Option("foo") :: Option(2) :: Option(3) :: HNil
    
    val l5 = l4 map get
    typed[ISII](l5)
    assertEquals(1 :: "foo" :: 2 :: 3 :: HNil, l5)
    
    typed[Int](l5.head)
    typed[String](l5.tail.head)
    typed[Int](l5.tail.tail.head)
    typed[Int](l5.tail.tail.tail.head)
    
    val l6 = l1 map identity
    typed[ISII](l6)
    assertEquals(1 :: "foo" :: 2 :: 3 :: HNil, l6)

    val l7 = l4 map isDefined
    typed[BBBB](l7)
    assertEquals(true :: true :: true :: true :: HNil, l7)
  }
  
  @Test
  def testConformance {
    val l1 = 1 :: "foo" :: 2 :: 3 :: HNil
    typed[Any :: AnyRef :: Any :: Any :: HNil](l1)
    assertEquals(1 :: "foo" :: 2 :: 3 :: HNil, l1)

    val ap = a :: p :: HNil
    typed[AP](ap)
    val bp = b :: p :: HNil
    typed[BP](bp)
    val apap = a :: p :: a :: p :: HNil
    typed[APAP](apap)
    val apbp = a :: p :: b :: p :: HNil
    typed[APBP](apbp)
    val ffff : FFFF = apap
    typed[FFFF](ffff)
  }
  
  @Test
  def testInitLast {
    
    val lp = apbp.last
    typed[Pear](lp)
    assertEquals(p, lp)
    
    val iapb = apbp.init
    typed[APB](iapb)
    assertEquals(a :: p :: b :: HNil, iapb)
  }
  
  @Test
  def testReverse {
    val pbpa = apbp.reverse
    typed[PBPA](pbpa)
    assertEquals(p :: b :: p :: a :: HNil, pbpa)

    val al = a :: HNil
    val ral = al.reverse
    typed[Apple :: HNil](ral)
    assertEquals(a :: HNil, ral)
  }
    
  @Test
  def testPrepend {
    val apbp2 = ap ::: bp
    typed[APBP](apbp2)
    assertEquals(a :: p :: b :: p :: HNil, apbp2)

    typed[Apple](apbp2.head)
    typed[Pear](apbp2.tail.head)
    typed[Banana](apbp2.tail.tail.head)
    typed[Pear](apbp2.tail.tail.tail.head)
    
    val pabp = ap reverse_::: bp
    typed[PABP](pabp)
    assertEquals(p :: a :: b :: p :: HNil, pabp)
  }
    
  @Test
  def testUnifier {
    def lub[X, Y, L](x : X, y : Y)(implicit lb : Lub[X, Y, L]) : (L, L) = (lb.left(x), lb.right(y))
    
    val u21 = lub(a, a)
    typed[(Apple, Apple)](u21)
    val u22 = lub(a, p)
    typed[(Fruit, Fruit)](u22)
    val u23 = lub(a, f)
    typed[(Fruit, Fruit)](u23)
    val u24 = lub(p, a)
    typed[(Fruit, Fruit)](u24)
    val u25 = lub(p, p)
    typed[(Pear, Pear)](u25)
    val u26 = lub(f, f)
    typed[(Fruit, Fruit)](u26)
    val u27 = lub(f, a)
    typed[(Fruit, Fruit)](u27)
    val u28 = lub(f, p)
    typed[(Fruit, Fruit)](u28)
    val u29 = lub(f, f)
    typed[(Fruit, Fruit)](u29)

    implicitly[Lub[HNil, HNil, HNil]]
    implicitly[Lub[Apple :: HNil, Apple :: HNil, Apple :: HNil]]
    implicitly[Lub[Fruit :: Pear :: HNil, Fruit :: Fruit :: HNil, Fruit :: Fruit :: HNil]]
    implicitly[Lub[Apple :: Pear :: HNil, Pear :: Apple :: HNil, Fruit :: Fruit :: HNil]]
    implicitly[Lub[ISII, IIII, IYII]]
    
    val u31 = lub(HNil, HNil)
    typed[(HNil, HNil)](u31)
    val u32 = lub(a :: HNil, a :: HNil)
    typed[(Apple :: HNil, Apple :: HNil)](u32)
    val u33 = lub(f :: p :: HNil, f :: f :: HNil)
    typed[(Fruit :: Fruit :: HNil, Fruit :: Fruit :: HNil)](u33)
    val u34 = lub(a :: p :: HNil, p :: a :: HNil)
    typed[(Fruit :: Fruit :: HNil, Fruit :: Fruit :: HNil)](u34)
    val u35 = lub(1 :: "two" :: 3 :: 4 :: HNil, 1 :: 2 :: 3 :: 4 :: HNil) 
    typed[(Int :: Any :: Int :: Int :: HNil, Int :: Any :: Int :: Int :: HNil)](u35)
    
    implicitly[UnifierAux[Apple :: HNil, Apple :: HNil]]
    implicitly[UnifierAux[Fruit :: Pear :: HNil, Fruit :: Fruit :: HNil]]
    implicitly[UnifierAux[Apple :: Pear :: HNil, Fruit :: Fruit :: HNil]]
    
    implicitly[UnifierAux[Int :: String :: Int :: Int :: HNil, YYYY]]
    
    val uapap = implicitly[UnifierAux[Apple :: Pear :: Apple :: Pear :: HNil, FFFF]]
    val unified1 = uapap(apap)
    typed[FFFF](unified1)
    val unified2 = apap.unify
    typed[FFFF](unified2)
    
    val ununified1 = unified2.cast[APAP]
    assertTrue(ununified1.isDefined)
    typed[APAP](ununified1.get)
    val ununified2 = unified2.cast[APBP]
    assertFalse(ununified2.isDefined)
    typed[Option[APBP]](ununified2)

    def getUnifier[L <: HList, Out <: HList](l : L)(implicit u : UnifierAux[L, Out]) = u
    
    val u2 = getUnifier(a :: HNil)
    typed[UnifierAux[Apple :: HNil, Apple :: HNil]](u2)
    val u3 = getUnifier(a :: a :: HNil)
    typed[UnifierAux[Apple :: Apple :: HNil, Apple :: Apple :: HNil]](u3)
    val u4 = getUnifier(a :: a :: a :: HNil)
    typed[UnifierAux[Apple :: Apple :: Apple :: HNil, Apple :: Apple :: Apple :: HNil]](u4)
    val u5 = getUnifier(a :: a :: a :: a :: HNil)
    typed[UnifierAux[Apple :: Apple :: Apple :: Apple :: HNil, Apple :: Apple :: Apple :: Apple :: HNil]](u5)
    val u6 = getUnifier(a :: p :: HNil)
    //typed[UnifierAux[Apple :: Pear :: HNil, Fruit :: Fruit :: HNil]](u6)
    val u7 = getUnifier(a :: f :: HNil)
    typed[UnifierAux[Apple :: Fruit :: HNil, Fruit :: Fruit :: HNil]](u7)
    val u8 = getUnifier(f :: a :: HNil)
    typed[UnifierAux[Fruit :: Apple :: HNil, Fruit :: Fruit :: HNil]](u8)
    val u9a = getUnifier(a :: f :: HNil)
    typed[UnifierAux[Apple :: Fruit :: HNil, FF]](u9a)
    val u9b = getUnifier(a :: p :: HNil)
    //typed[UnifierAux[Apple :: Pear :: HNil, FF]](u9b)
    val u10 = getUnifier(apap)
    //typed[UnifierAux[APAP, FFFF]](u10)
    val u11 = getUnifier(apbp)
    //typed[UnifierAux[APBP, FFFF]](u11)
  }
    
  @Test
  def testToList {
    val fruits1 = apap.toList
    typed[List[Fruit]](fruits1)
    assertEquals(List(a, p, a, p), fruits1)
    
    val fruits2 = apbp.toList
    typed[List[Fruit]](fruits2)
    assertEquals(List(a, p, b, p), fruits2)
    
    val fruits3 = fruits2.toHList[APBP]
    assertTrue(fruits3.isDefined)
    typed[APBP](fruits3.get)
    assertEquals(apbp, fruits3.get)

    val l1 = 1 :: "foo" :: 2 :: 3 :: HNil

    val stuff = l1.toList
    typed[List[Any]](stuff)
    assertEquals(List(1, "foo", 2, 3), stuff)
    
    val stuff2 = stuff.toHList[ISII]
    assertTrue(stuff2.isDefined)
    typed[ISII](stuff2.get)
    assertEquals(1 :: "foo" :: 2 :: 3 :: HNil, stuff2.get)

    val l4 = Option(1) :: Option("foo") :: Option(2) :: Option(3) :: HNil
    val l7 = l4 map isDefined
    typed[BBBB](l7)
    assertEquals(true :: true :: true :: true :: HNil, l7)

    val ll2 = l7.toList
    typed[Boolean](ll2.head)

    val moreStuff = (a :: "foo" :: p :: HNil).toList
    typed[List[Any]](moreStuff)
  }
  
  @Test
  def testFoldLeft {
    implicitly[MapperAux[isDefined.type, HNil, HNil]]
    implicitly[MapperAux[isDefined.type, Option[Int] :: HNil, Boolean :: HNil]]
    
    val tl1 = Option(1) :: Option("foo") :: Option(2) :: Option(3) :: HNil 
    val tl2 = Option(1) :: Option("foo") :: (None : Option[Int]) :: Option(3) :: HNil
    
    val mlfl1 = (tl1 map isDefined).toList.foldLeft(true)(_ && _)
    assertTrue(mlfl1)
    val mlfl2 = (tl2 map isDefined).toList.foldLeft(true)(_ && _)
    assertFalse(mlfl2)
    
    val fl1 = tl1.foldLeft(true)(isDefined)(_ && _)
    assertTrue(fl1)
    val fl2 = tl2.foldLeft(true)(isDefined)(_ && _)
    assertFalse(fl2)
  }
  
  @Test
  def testAt {
    val sn1 = 23 :: 3.0 :: "foo" :: () :: "bar" :: true :: 5L :: HNil
    
    val at0 = sn1(_0)
    typed[Int](at0)
    assertEquals(23, at0)
    
    val at1 = sn1(_1)
    typed[Double](at1)
    assertEquals(3.0, at1, Double.MinPositiveValue)
    
    val at2 = sn1(_2)
    typed[String](at2)
    assertEquals("foo", at2)
    
    val at3 = sn1(_3)
    typed[Unit](at3)
    assertEquals((), at3)
    
    val at4 = sn1(_4)
    typed[String](at4)
    assertEquals("bar", at4)
    
    val at5 = sn1(_5)
    typed[Boolean](at5)
    assertEquals(true, at5)
    
    val at6 = sn1(_6)
    typed[Long](at6)
    assertEquals(5L, at6)
    
    val sn2 =
      0 :: 1 :: 2 :: 3 :: 4 :: 5 :: 6 :: 7 :: 8 :: 9 :: 
      10 :: 11 :: 12 :: 13 :: 14 :: 15 :: 16 :: 17 :: 18 :: 19 :: 
      20 :: 21 :: 22 :: HNil
      
    val at22 = sn2(_22)
    typed[Int](at22)
    assertEquals(22, at22)
  }
  
  @Test
  def testTakeDrop {
    val sn1 = 23 :: 3.0 :: "foo" :: () :: "bar" :: true :: 5L :: HNil
    
    val t0 = sn1.take(_0)
    typed[HNil](t0)
    assertEquals(HNil, t0)
    
    val d0 = sn1.drop(_0)
    typed[Int :: Double :: String :: Unit :: String :: Boolean :: Long :: HNil](d0)
    assertEquals(23 :: 3.0 :: "foo" :: () :: "bar" :: true :: 5L :: HNil, d0)

    val t2 = sn1.take(_2)
    typed[Int :: Double :: HNil](t2)
    assertEquals(23 :: 3.0 :: HNil, t2)
    
    val d2 = sn1.drop(_2)
    typed[String :: Unit :: String :: Boolean :: Long :: HNil](d2)
    assertEquals("foo" :: () :: "bar" :: true :: 5L :: HNil, d2)

    val t7 = sn1.take(_7)
    typed[Int :: Double :: String :: Unit :: String :: Boolean :: Long :: HNil](t7)
    assertEquals(23 :: 3.0 :: "foo" :: () :: "bar" :: true :: 5L :: HNil, t7)
    
    val d7 = sn1.drop(_7)
    typed[HNil](d7)
    assertEquals(HNil, d7)
  }
  
  @Test
  def testSplit {
    val sn1 = 23 :: 3.0 :: "foo" :: () :: "bar" :: true :: 5L :: HNil

    val sni0 = sn1.split(_0)
    typed[(HNil, Int :: Double :: String :: Unit :: String :: Boolean :: Long :: HNil)](sni0)
    val sni1 = sn1.split(_1)
    typed[(Int :: HNil, Double :: String :: Unit :: String :: Boolean :: Long :: HNil)](sni1)
    val sni2 = sn1.split(_2)
    typed[(Int :: Double :: HNil, String :: Unit :: String :: Boolean :: Long :: HNil)](sni2)
    val sni3 = sn1.split(_3)
    typed[(Int :: Double :: String :: HNil, Unit :: String :: Boolean :: Long :: HNil)](sni3)
    val sni4 = sn1.split(_4)
    typed[(Int :: Double :: String :: Unit :: HNil, String :: Boolean :: Long :: HNil)](sni4)
    val sni5 = sn1.split(_5)
    typed[(Int :: Double :: String :: Unit :: String :: HNil, Boolean :: Long :: HNil)](sni5)
    val sni6 = sn1.split(_6)
    typed[(Int :: Double :: String :: Unit :: String :: Boolean :: HNil, Long :: HNil)](sni6)
    val sni7 = sn1.split(_7)
    typed[(Int :: Double :: String :: Unit :: String :: Boolean :: Long :: HNil, HNil)](sni7)
    
    val snri0 = sn1.reverse_split(_0)
    typed[(HNil, Int :: Double :: String :: Unit :: String :: Boolean :: Long :: HNil)](snri0)
    val snri1 = sn1.reverse_split(_1)
    typed[(Int :: HNil, Double :: String :: Unit :: String :: Boolean :: Long :: HNil)](snri1)
    val snri2 = sn1.reverse_split(_2)
    typed[(Double :: Int :: HNil, String :: Unit :: String :: Boolean :: Long :: HNil)](snri2)
    val snri3 = sn1.reverse_split(_3)
    typed[(String :: Double :: Int :: HNil, Unit :: String :: Boolean :: Long :: HNil)](snri3)
    val snri4 = sn1.reverse_split(_4)
    typed[(Unit :: String :: Double :: Int :: HNil, String :: Boolean :: Long :: HNil)](snri4)
    val snri5 = sn1.reverse_split(_5)
    typed[(String :: Unit :: String :: Double :: Int :: HNil, Boolean :: Long :: HNil)](snri5)
    val snri6 = sn1.reverse_split(_6)
    typed[(Boolean :: String :: Unit :: String :: Double :: Int :: HNil, Long :: HNil)](snri6)
    val snri7 = sn1.reverse_split(_7)
    typed[(Long :: Boolean :: String :: Unit :: String :: Double :: Int :: HNil, HNil)](snri7)
  }
  
  @Test
  def testSelect {
    val sl = 1 :: true :: "foo" :: 2.0 :: HNil
    val si = sl.select[Int]
    typed[Int](si)
    assertEquals(1, si)
    
    val sb = sl.select[Boolean]
    typed[Boolean](sb)
    assertEquals(true, sb)

    val ss = sl.select[String]
    typed[String](ss)
    assertEquals("foo", ss)

    val sd = sl.select[Double]
    typed[Double](sd)
    assertEquals(2.0, sd, Double.MinPositiveValue)
  }
  
  @Test
  def testReplace {
    val sl = 1 :: true :: "foo" :: 2.0 :: HNil
    
    val (i, r1) = sl.replace(23)
    typed[Int](i)
    assertEquals(1, i)
    assertEquals(23 :: true :: "foo" :: 2.0 :: HNil, r1)
    
    val (b, r2) = sl.replace(false)
    typed[Boolean](b)
    assertEquals(true, b)
    assertEquals(1 :: false :: "foo" :: 2.0 :: HNil, r2)

    val (s, r3) = sl.replace("bar")
    typed[String](s)
    assertEquals("foo", s)
    assertEquals(1 :: true :: "bar" :: 2.0 :: HNil, r3)

    val (d, r4) = sl.replace(3.0)
    typed[Double](d)
    assertEquals(2.0, d, Double.MinPositiveValue)
    assertEquals(1 :: true :: "foo" :: 3.0 :: HNil, r4)
    
    val (i2, r5) = sl.replaceType[Int]('*')
    typed[Int](i2)
    typed[Char](r5(_0))
    assertEquals(1, i2)
    assertEquals('*' :: true :: "foo" :: 2.0 :: HNil, r5)

    val (b2, r6) = sl.replaceType[Boolean]('*')
    typed[Boolean](b2)
    typed[Char](r6(_1))
    assertEquals(true, b2)
    assertEquals(1 :: '*' :: "foo" :: 2.0 :: HNil, r6)

    val (s2, r7) = sl.replaceType[String]('*')
    typed[String](s2)
    typed[Char](r7(_2))
    assertEquals("foo", s2)
    assertEquals(1 :: true :: '*' :: 2.0 :: HNil, r7)

    val (d2, r8) = sl.replaceType[Double]('*')
    typed[Double](d2)
    typed[Char](r8(_3))
    assertEquals(2.0, d2, Double.MinPositiveValue)
    assertEquals(1 :: true :: "foo" :: '*' :: HNil, r8)
    
    val fruits = a :: p :: a :: f :: HNil
    val (x1, rr1) = fruits.replaceType[Pear](a)
    typed[Pear](x1)
    typed[Apple :: Apple :: Apple :: Fruit :: HNil](rr1)
    
    val (x2, rr2) = fruits.replaceType[Pear](f)
    typed[Pear](x2)
    typed[Apple :: Fruit :: Apple :: Fruit :: HNil](rr2)
    
    val (x3, rr3) = fruits.replaceType[Fruit](p)
    typed[Fruit](x3)
    typed[Apple :: Pear :: Apple :: Pear :: HNil](rr3)
    
    val (x4, rr4) = fruits.replace(p)
    typed[Pear](x4)
    typed[Apple :: Pear :: Apple :: Fruit :: HNil](rr4)
    
    val (x5, rr5) = fruits.replace(f)
    typed[Fruit](x5)
    typed[Apple :: Pear :: Apple :: Fruit :: HNil](rr5)
  }
  
  @Test
  def testUpdate {
    val sl = 1 :: true :: "foo" :: 2.0 :: HNil
    
    val r1 = sl.updatedElem(23)
    assertEquals(23 :: true :: "foo" :: 2.0 :: HNil, r1)
    
    val r2 = sl.updatedElem(false)
    assertEquals(1 :: false :: "foo" :: 2.0 :: HNil, r2)

    val r3 = sl.updatedElem("bar")
    assertEquals(1 :: true :: "bar" :: 2.0 :: HNil, r3)

    val r4 = sl.updatedElem(3.0)
    assertEquals(1 :: true :: "foo" :: 3.0 :: HNil, r4)
    
    val r5 = sl.updatedType[Int]('*')
    assertEquals('*' :: true :: "foo" :: 2.0 :: HNil, r5)

    val r6 = sl.updatedType[Boolean]('*')
    assertEquals(1 :: '*' :: "foo" :: 2.0 :: HNil, r6)

    val r7 = sl.updatedType[String]('*')
    assertEquals(1 :: true :: '*' :: 2.0 :: HNil, r7)

    val r8 = sl.updatedType[Double]('*')
    assertEquals(1 :: true :: "foo" :: '*' :: HNil, r8)
    
    val fruits = a :: p :: a :: f :: HNil
    
    val rr1 = fruits.updatedType[Pear](a)
    typed[Apple :: Apple :: Apple :: Fruit :: HNil](rr1)
    
    val rr2 = fruits.updatedType[Pear](f)
    typed[Apple :: Fruit :: Apple :: Fruit :: HNil](rr2)
    
    val rr3 = fruits.updatedType[Fruit](p)
    typed[Apple :: Pear :: Apple :: Pear :: HNil](rr3)
    
    val rr4 = fruits.updatedElem(p)
    typed[Apple :: Pear :: Apple :: Fruit :: HNil](rr4)
    
    val rr5 = fruits.updatedElem(f)
    typed[Apple :: Pear :: Apple :: Fruit :: HNil](rr5)
  }

  @Test
  def testSplitLeft {
    val sl = 1 :: true :: "foo" :: 2.0 :: HNil
    val sl2 = 23 :: 3.0 :: "foo" :: () :: "bar" :: true :: 5L :: HNil
    
    val (sp1, sp2) = sl.splitLeft[String]
    typed[Int :: Boolean :: HNil](sp1)
    typed[String :: Double :: HNil](sp2)
    assertEquals((sp1 ::: sp2), sl)

    val (sli1, sli2) = sl2.splitLeft[String]
    typed[Int :: Double :: HNil](sli1) 
    typed[String :: Unit :: String :: Boolean :: Long :: HNil](sli2)
    assertEquals((sli1 ::: sli2), sl2)

    val (rsp1, rsp2) = sl.reverse_splitLeft[String]
    typed[Boolean :: Int :: HNil](rsp1)
    typed[String :: Double :: HNil](rsp2)
    assertEquals((rsp1 reverse_::: rsp2), sl)

    val (rsli1, rsli2) = sl2.reverse_splitLeft[String]
    typed[Double :: Int :: HNil](rsli1) 
    typed[String :: Unit :: String :: Boolean :: Long :: HNil](rsli2)
    assertEquals((rsli1 reverse_::: rsli2), sl2)

  }
  
  @Test
  def testSplitRight {
    val sl = 1 :: true :: "foo" :: 2.0 :: HNil
    val sl2 = 23 :: 3.0 :: "foo" :: () :: "bar" :: true :: 5L :: HNil

    val (srp1, srp2) = sl.splitRight[String]
    typed[Int :: Boolean :: String :: HNil](srp1)
    typed[Double :: HNil](srp2)
    assertEquals((srp1 ::: srp2), sl)

    val (srli1, srli2) = sl2.splitRight[String]
    typed[Int :: Double :: String :: Unit :: String :: HNil](srli1) 
    typed[Boolean :: Long :: HNil](srli2)
    assertEquals(sl2, srli1 ::: srli2)

    val (rsrp1, rsrp2) = sl.reverse_splitRight[String]
    typed[String :: Boolean :: Int :: HNil](rsrp1)
    typed[Double :: HNil](rsrp2)
    assertEquals((rsrp1 reverse_::: rsrp2), sl)

    val (rsrli1, rsrli2) = sl2.reverse_splitRight[String]
    typed[String :: Unit :: String :: Double :: Int :: HNil](rsrli1) 
    typed[Boolean :: Long :: HNil](rsrli2)
    assertEquals((rsrli1 reverse_::: rsrli2), sl2)
  }
  
  @Test
  def testTranspose {
    val l1 = 1 :: HNil
    val l2 = ("a" :: HNil) :: HNil
    
    val z1 = l1.zipOne(l2)
    typed[(Int :: String :: HNil) :: HNil](z1)
    assertEquals((1 :: "a" :: HNil) :: HNil, z1)
    
    val mc1 = l1.mapConst(HNil)
    typed[HNil :: HNil](mc1)
    assertEquals(HNil :: HNil, mc1)
    
    val t1 = (l1 :: HNil).transpose
    typed[(Int :: HNil) :: HNil](t1)
    assertEquals((1 :: HNil) :: HNil, t1)

    val l3 = 1 :: 2 :: 3 :: HNil
    val l4 = ("a" :: 1.0 :: HNil) :: ("b" :: 2.0 :: HNil) :: ("c" :: 3.0 :: HNil) :: HNil
    
    val z2 = l3.zipOne(l4)
    typed[(Int :: String :: Double :: HNil) :: (Int :: String :: Double :: HNil) :: (Int :: String :: Double :: HNil) :: HNil](z2)
    assertEquals((1 :: "a" :: 1.0 :: HNil) :: (2 :: "b" :: 2.0 :: HNil) :: (3 :: "c" :: 3.0 :: HNil) :: HNil, z2)
    
    val mc2 = l3.mapConst(HNil)
    typed[HNil :: HNil :: HNil :: HNil](mc2)
    assertEquals(HNil :: HNil :: HNil :: HNil, mc2)
    
    val t2 = l4.transpose
    typed[(String :: String :: String :: HNil) :: (Double :: Double :: Double :: HNil) :: HNil](t2)
    assertEquals(("a" :: "b" :: "c" :: HNil) :: (1.0 :: 2.0 :: 3.0 :: HNil) :: HNil, t2)
    
    val t3 = z2.transpose
    typed[(Int :: Int :: Int :: HNil) :: (String :: String :: String :: HNil) :: (Double :: Double :: Double :: HNil) :: HNil](t3)
    assertEquals((1 :: 2 :: 3 :: HNil) :: ("a" :: "b" :: "c" :: HNil) :: (1.0 :: 2.0 :: 3.0 :: HNil) :: HNil, t3)
    
    val t4 = t3.transpose
    typed[(Int :: String :: Double :: HNil) :: (Int :: String :: Double :: HNil) :: (Int :: String :: Double :: HNil) :: HNil](t4)
    assertEquals(z2, t4)
  }
  
  @Test
  def testZipUnzip {
    val l1 = 1 :: "a" :: 1.0 :: HNil
    val l2 = 2 :: "b" :: 2.0 :: HNil
    
    val t1 = (l1 :: l2 :: HNil).transpose
    val z1 = t1.map(tupled)
    typed[(Int, Int) :: (String, String) :: (Double, Double) :: HNil](z1)
    assertEquals((1, 2) :: ("a", "b") :: (1.0, 2.0) :: HNil, z1)
    
    def zip[L <: HList, OutT <: HList, OutM <: HList](l : L)
      (implicit
        transposer : TransposerAux[L, OutT],
        mapper : MapperAux[tupled.type, OutT, OutM]) = l.transpose.map(tupled)
    
    val z2 = zip(l1 :: l2 :: HNil)
    typed[(Int, Int) :: (String, String) :: (Double, Double) :: HNil](z2)
    assertEquals((1, 2) :: ("a", "b") :: (1.0, 2.0) :: HNil, z2)

    val z3 = (l1 :: l2 :: HNil).zipped
    typed[(Int, Int) :: (String, String) :: (Double, Double) :: HNil](z3)
    assertEquals((1, 2) :: ("a", "b") :: (1.0, 2.0) :: HNil, z3)
    
    val t2 = z1.map(hlisted).transpose
    val u1 = t2.tupled
    typed[(Int :: String :: Double :: HNil, Int :: String :: Double :: HNil)](u1)
    assertEquals((1 :: "a" :: 1.0 :: HNil, 2 :: "b" :: 2.0 :: HNil), u1)

    def unzip[L <: HList, OutM <: HList, OutT <: HList](l : L)
      (implicit
        mapper : MapperAux[hlisted.type, L, OutM],
        transposer : TransposerAux[OutM, OutT],
        tupler : Tupler[OutT]) = l.map(hlisted).transpose.tupled
        
    val u2 = unzip(z1)
    typed[(Int :: String :: Double :: HNil, Int :: String :: Double :: HNil)](u2)
    assertEquals((1 :: "a" :: 1.0 :: HNil, 2 :: "b" :: 2.0 :: HNil), u2)
    
    val u3 = z1.unzipped
    typed[(Int :: String :: Double :: HNil, Int :: String :: Double :: HNil)](u3)
    assertEquals((1 :: "a" :: 1.0 :: HNil, 2 :: "b" :: 2.0 :: HNil), u3)
    
    val z4 = l1 zip l2
    typed[(Int, Int) :: (String, String) :: (Double, Double) :: HNil](z4)
    assertEquals((1, 2) :: ("a", "b") :: (1.0, 2.0) :: HNil, z4)

    val intInc : Int => Int = _+1
    val stringInc : String => String = _+"*"
    val doubleInc : Double => Int = _.toInt+1
    
    val l3 = intInc :: stringInc :: doubleInc :: HNil
    
    val z5 = l3 zipApply l1
    typed[Int :: String :: Int :: HNil](z5)
    assertEquals(2 :: "a*" :: 2 :: HNil, z5)
  }
  
  @Test
  def testUnapply {
    val l = 1 :: true :: "foo" :: 2.0 :: HNil
    val l2 = 23 :: 3.0 :: "foo" :: () :: "bar" :: true :: 5L :: HNil
    
    val is = l match {
      case i :: true :: s :: 2.0 :: HNil => (i, s) 
    }
    
    typed[(Int, String)](is)
    assertEquals(1, is._1)
    assertEquals("foo", is._2)

    val is2 = (l : Any) match {
      case (i : Int) :: true :: (s : String) :: 2.0 :: HNil => (i, s)
      case _ => sys.error("Not matched")
    }

    typed[(Int, String)](is2)
    assertEquals(1, is2._1)
    assertEquals("foo", is2._2)
    
    import ListCompat._
    
    val tl = l2 match {
      case 23 #: 3.0 #: s #: xs => (s, xs)
    }
    
    typed[(String, Unit :: String :: Boolean :: Long :: HNil)](tl)
    assertEquals("foo", tl._1)
    assertEquals(() :: "bar" :: true :: 5L :: HNil, tl._2)

    val tl2 = (l2 : Any) match {
      case 23 #: 3.0 #: (s : String) #: xs => (s, xs)
      case _ => sys.error("Not matched")
    }
    
    typed[(String, HList)](tl2)
    assertEquals("foo", tl2._1)
    assertEquals(() :: "bar" :: true :: 5L :: HNil, tl2._2)
    
    val ll = List(1, 2, 3, 4)
    val tll = ll match {
      case 1 :: 2 :: x :: y :: Nil => (x, y)
      case _ => sys.error("Not matched")
    }
    typed[(Int, Int)](tll)
    assertEquals(3, tll._1)
    assertEquals(4, tll._2)
    
    val tll2 = ll match {
      case 1 :: xs => xs
      case _ => sys.error("Not matched")
    }
    typed[List[Int]](tll2)
    assertEquals(List(2, 3, 4), tll2)
    
    val mixed = 23 :: "foo" :: (1 :: 2 :: 3 :: 4 :: 5 :: Nil) :: false :: () :: HNil
    val tmixed = mixed match {
      case _ #: _ #: (_ :: 2 :: x :: tl1) #: tl2 => (x, tl1, tl2) 
      case _ => sys.error("Not matched")
    }
    typed[(Int, List[Int], Boolean :: Unit :: HNil)](tmixed)
    assertEquals(3, tmixed._1)
    assertEquals(4 :: 5 :: Nil, tmixed._2)
    assertEquals(false :: () :: HNil, tmixed._3)
  }

  @Test
  def testRemove {
    val l = 1 :: true :: "foo" :: HNil

    val li = l.removeElem[Int]
    typed[(Int, Boolean :: String :: HNil)](li)
    assertEquals((1, true :: "foo" :: HNil), li)

    val lb = l.removeElem[Boolean]
    typed[(Boolean, Int :: String :: HNil)](lb)
    assertEquals((true, 1 :: "foo" :: HNil), lb)

    val ls = l.removeElem[String]
    typed[(String, Int :: Boolean :: HNil)](ls)
    assertEquals(("foo", 1 :: true :: HNil), ls)
  }

  @Test
  def testRemoveAll {
    val l = 1 :: true :: "foo" :: HNil

    val li = l.removeAll[Int :: HNil]
    typed[(Int :: HNil, Boolean :: String :: HNil)](li)
    assertEquals((1 :: HNil, true :: "foo" :: HNil), li)

    val lb = l.removeAll[Boolean :: HNil]
    typed[(Boolean :: HNil, Int :: String :: HNil)](lb)
    assertEquals((true :: HNil, 1 :: "foo" :: HNil), lb)

    val lbi = l.removeAll[Boolean :: Int :: HNil]
    typed[(Boolean :: Int :: HNil, String :: HNil)](lbi)
    assertEquals((true :: 1 :: HNil, "foo" :: HNil), lbi)
  }
}
