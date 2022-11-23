package lucenex;

import java.awt.List;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.StringValueExp;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.tests.analysis.TokenStreamToDot;
import org.junit.Test;
import org.w3c.dom.Text;




public class HomeWork1{
	
	public static void main (String args[]) throws IOException {
		
		File folder = new File("FileTxt/");
		
		Path path = Paths.get("targetHW1//");
		Directory directory = FSDirectory.open(path);	

		CharArraySet stopWords = new CharArraySet(
				Arrays.asList("di","a","da","in","con","su","per","tra","fra","dei","delle","della"), true);
		
		Map<String, Analyzer> perFielAnalyzers = new HashMap<>();
		perFielAnalyzers.put("Nome File", new WhitespaceAnalyzer());
		perFielAnalyzers.put("contenuto", new StandardAnalyzer(stopWords));
		
		
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new ItalianAnalyzer(), perFielAnalyzers);
	
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCodec(new SimpleTextCodec());
		
		IndexWriter writer = new IndexWriter(directory,config);
		
		/*Analyzer a = CustomAnalyzer.builder()
				.withTokenizer(WhitespaceTokenizerFactory.class)
				.addTokenFilter(LowerCaseFilterFactory.class)
				.addTokenFilter(WordDelimiterGraphFilterFactory.class)
				.build();
		*/
		for(File f : findAllFilesInFolder(folder)) {
		
			String content = new String(Files.readAllBytes(Paths.get(f.getPath())), StandardCharsets.ISO_8859_1);
			System.out.println(f.getName().replace(".txt", ""));
			System.out.println(content);
			Document d1 = new Document();
			Document d2 = new Document();
			d1.add(new TextField("Nome File", f.getName().replace(".txt", ""), Field.Store.YES));
			d2.add(new TextField("Contenuto", content, Field.Store.YES));
			writer.addDocument(d1);
			writer.addDocument(d2);
			
		}
		
		writer.commit();

		writer.close();

		

		
		
		
	}

public static ArrayList<File> findAllFilesInFolder(File folder) {
	
	ArrayList<File> files = new ArrayList<File>();
	
	for (File file : folder.listFiles()) {
		if (!file.isDirectory()) {
			files.add(file);
		} else {
			findAllFilesInFolder(file);
		}
	}
	return files;
}
}




