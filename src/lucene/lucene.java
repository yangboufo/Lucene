package lucene;

import java.io.File;  
import java.io.IOException; 
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.analysis.Analyzer;    
import org.apache.lucene.analysis.TokenStream;

import org.apache.lucene.index.CorruptIndexException;  
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;  
import org.apache.lucene.index.IndexWriterConfig;  
import org.apache.lucene.index.IndexWriterConfig.OpenMode;  
import org.apache.lucene.index.StoredDocument;
import org.apache.lucene.store.Directory;  
import org.apache.lucene.store.FSDirectory;  
import org.apache.lucene.store.LockObtainFailedException;  
import org.apache.lucene.util.Version;  
import org.apache.lucene.document.Document;  
import org.apache.lucene.document.Field;  
import org.apache.lucene.document.FieldType;;
  


public class lucene {
	 String docsPath;
	 String indexPath; //indexPath
	 Analyzer analyzer;
	 Directory index;
	 IndexWriterConfig config;
	 IndexWriter w;
	 DirectoryReader reader;
	 int hitsPerPage;
	 Query q;
	 ScoreDoc[] hits;
	 IndexSearcher searcher;
	 ExtractText HTMLText;	
	 String field;
	 String input;
	 List searchResult;
	 String searchWord;
	 public lucene(String searchWord)
	 {
		 this.analyzer = new StandardAnalyzer(Version.LUCENE_50);
		 this.searchWord = searchWord;
		 this.searchResult = new ArrayList();
		 this.HTMLText = new ExtractText();
		 this.hitsPerPage = 10;
		 this.field ="field";
		 
		 try {
			this.index();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 public void search() {
		 try {  
			    //initial the index part
	            //test.index(); 
	            //input the query string
	            //System.out.println("Please input"); //input the string which you want to search
	            //String input=new String(); 
	            //Scanner systemInput=new Scanner(System.in); 
	            //input=systemInput.next(); 
	            String querystr = searchWord;
	            q = new QueryParser(Version.LUCENE_50, field, analyzer).parse(querystr);
	            
				reader= DirectoryReader.open(index);
				searcher = new IndexSearcher(reader);
				
				TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);//return top N 
				searcher.search(q, collector);
				hits = collector.topDocs().scoreDocs;//find 'hits' record
				System.out.println("Found " + hits.length + " hits.");				
				 for(int i=0;i<hits.length;++i) {
				     int docId = hits[i].doc;
				     StoredDocument d = searcher.doc(docId);
				     String text =  d.get(field); //get the content in this field'
				     SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");    
				     Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(q));
				     highlighter.setTextFragmenter(new SimpleFragmenter(text.length())); 
				     if (text != null) {    
			                TokenStream tokenStream = analyzer.tokenStream(field,new StringReader(text));    
			                String highLightText = "";
							try {
								highLightText = highlighter.getBestFragment(tokenStream, text);
							} catch (InvalidTokenOffsetsException e) {
								e.printStackTrace();
							} 
							highLightText = "★HighLight "+(i+1) +" list："+highLightText;
			                //System.out.println(highLightText);    
							searchResult.add(highLightText);
			            } 
				     
				 }
				
	        } catch (CorruptIndexException e) {  
	            
	            e.printStackTrace();  
	        } catch (LockObtainFailedException e) {  
	           
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            
	            e.printStackTrace();  
	        }  
		    catch (ParseException e) {
				e.printStackTrace();
		    }
		 
		
		
	 }
	 
	
	 public void index() throws CorruptIndexException, LockObtainFailedException, IOException  
	 {
		 docsPath = "/Users/yangbo/Documents/NSFabstract/Part1/awards_1990/awd_1990_00";
		 indexPath = "/Users/yangbo/Documents/index";
		 File docDir = new File(docsPath);  
		 File[] docFiles = docDir.listFiles();
		 File indexDir = new File(indexPath);
		 index = FSDirectory.open(indexDir);
		 config = new IndexWriterConfig(Version.LUCENE_50, analyzer);
		 w = new IndexWriter(index, config);
		 if(docsPath==null)  
		 {  
			 System.err.println("docsPath is null");  
			 System.exit(1);  
		 }
		 for(int i=0;i<docFiles.length ;i++)
		 {
			 if(docFiles[i].isFile()&&docFiles[i].getName().endsWith(".txt"))
			 {
				 System.out.println( " File  "   +  docFiles[i].getCanonicalPath()   +   "is now indexed . " );   
				 try {
					 HTMLText.HTMLtoText(docFiles[i].getPath());
					 } catch (Exception e) {
					e.printStackTrace();
					}
				 addDoc(w, HTMLText.WebContent);
			 }
		 }
		
		 w.close();
	 }
	 public void addDoc(IndexWriter writer, String text) throws IOException {
		    Document doc = new Document();
		    FieldType fType = new FieldType();
		    fType.setIndexed(true);
		    fType.setStored(true);
		    doc.add(new Field("field", text, fType));
		    writer.addDocument(doc);
		  }
}
