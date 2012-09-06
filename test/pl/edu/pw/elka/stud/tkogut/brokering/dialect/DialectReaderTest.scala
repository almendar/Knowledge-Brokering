package pl.edu.pw.elka.stud.tkogut.brokering.dialect

import org.scalatest.FunSuite

class DialectReaderTest extends FunSuite {
   
  val reader = new DialectReader
  final val FILE_NAME = "passim-dialect.xml"
      
  test("Loading file") {
    assert(reader.root == null)
    reader.read(FILE_NAME)
    assert(reader.root!=null)  
  }
  
  test("Loading Dialect name") {
    reader.loadDialectName()
    assert(reader.dialectName == "Passim-Dialect") 
  }
  		  
  test("Loading entities") {
    reader.loadEntites()
    assert(reader.entitesSeq != null)
    assert(reader.entitesSeq.length == 1)    
    assert(reader.entites.length == 4)
  }
  
  test("Loading mappings") {
    reader.loadMappings()
    assert(reader.mappings!=null)
    assert(reader.mappings.length == 1)
  }
  	
}