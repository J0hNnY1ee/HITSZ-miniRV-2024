file://<WORKSPACE>/build.sc
### java.lang.RuntimeException: ambiguous mend file%3A%2F%2F%2Fhome%2Fj0hnny%2FPrograming%2FCPU%2FHITSZ-miniRV-2024%2Fbuild.sc@826..826  file%3A%2F%2F%2Fhome%2Fj0hnny%2FPrograming%2FCPU%2FHITSZ-miniRV-2024%2Fbuild.sc@822..826 main

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 2.13.14
Classpath:
<HOME>/.cache/coursier/v1/https/repo.huaweicloud.com/repository/maven/org/scala-lang/scala-library/2.13.14/scala-library-2.13.14.jar [exists ]
Options:



action parameters:
uri: file://<WORKSPACE>/build.sc
text:
```scala
// import Mill dependency
import mill._
import mill.define.Sources
import mill.modules.Util
import mill.scalalib.TestModule.ScalaTest
import scalalib._
// support BSP
import mill.bsp._

object miniRV extends SbtModule { m =>
  override def millSourcePath = os.pwd
  override def scalaVersion = "2.13.14"
  override def scalacOptions = Seq(
    "-language:reflectiveCalls",
    "-deprecation",
    "-feature",
    "-Xcheckinit",
  )
  override def ivyDeps = Agg(
    ivy"org.chipsalliance::chisel:6.5.0",
  )
  override def scalacPluginIvyDeps = Agg(
    ivy"org.chipsalliance:::chisel-plugin:6.5.0",
  )
  object test extends SbtModuleTests with TestModule.ScalaTest {
    override def ivyDeps = m.ivyDeps() ++ Agg(
      ivy"org.scalatest::scalatest::3.2.19",
      ivy"edu.berkeley.cs::chiseltest:6.0.0"
    )
  }
  def main
}

```



#### Error stacktrace:

```
scala.sys.package$.error(package.scala:27)
	scala.meta.internal.semanticdb.scalac.TextDocumentOps$XtensionCompilationUnitDocument$traverser$1$.indexName(TextDocumentOps.scala:86)
	scala.meta.internal.semanticdb.scalac.TextDocumentOps$XtensionCompilationUnitDocument$traverser$1$.apply(TextDocumentOps.scala:170)
	scala.meta.transversers.Traverser.applyRest(Traverser.scala:4)
	scala.meta.transversers.Traverser.apply(Traverser.scala:4)
	scala.meta.internal.semanticdb.scalac.TextDocumentOps$XtensionCompilationUnitDocument$traverser$1$.apply(TextDocumentOps.scala:175)
	scala.meta.transversers.Traverser.$anonfun$apply$1(Traverser.scala:4)
	scala.meta.transversers.Traverser.$anonfun$apply$1$adapted(Traverser.scala:4)
	scala.collection.immutable.List.foreach(List.scala:334)
	scala.meta.transversers.Traverser.apply(Traverser.scala:4)
	scala.meta.transversers.Traverser.applyRest(Traverser.scala:4)
	scala.meta.transversers.Traverser.apply(Traverser.scala:4)
	scala.meta.internal.semanticdb.scalac.TextDocumentOps$XtensionCompilationUnitDocument$traverser$1$.apply(TextDocumentOps.scala:175)
	scala.meta.transversers.Traverser.applyDefn(Traverser.scala:4)
	scala.meta.transversers.Traverser.apply(Traverser.scala:4)
	scala.meta.internal.semanticdb.scalac.TextDocumentOps$XtensionCompilationUnitDocument$traverser$1$.apply(TextDocumentOps.scala:175)
	scala.meta.transversers.Traverser.$anonfun$apply$1(Traverser.scala:4)
	scala.meta.transversers.Traverser.$anonfun$apply$1$adapted(Traverser.scala:4)
	scala.collection.immutable.List.foreach(List.scala:334)
	scala.meta.transversers.Traverser.apply(Traverser.scala:4)
	scala.meta.transversers.Traverser.applyRest(Traverser.scala:4)
	scala.meta.transversers.Traverser.apply(Traverser.scala:4)
	scala.meta.internal.semanticdb.scalac.TextDocumentOps$XtensionCompilationUnitDocument$traverser$1$.apply(TextDocumentOps.scala:175)
	scala.meta.internal.semanticdb.scalac.TextDocumentOps$XtensionCompilationUnitDocument.toTextDocument(TextDocumentOps.scala:179)
	scala.meta.internal.pc.SemanticdbTextDocumentProvider.textDocument(SemanticdbTextDocumentProvider.scala:54)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticdbTextDocument$1(ScalaPresentationCompiler.scala:462)
```
#### Short summary: 

java.lang.RuntimeException: ambiguous mend file%3A%2F%2F%2Fhome%2Fj0hnny%2FPrograming%2FCPU%2FHITSZ-miniRV-2024%2Fbuild.sc@826..826  file%3A%2F%2F%2Fhome%2Fj0hnny%2FPrograming%2FCPU%2FHITSZ-miniRV-2024%2Fbuild.sc@822..826 main