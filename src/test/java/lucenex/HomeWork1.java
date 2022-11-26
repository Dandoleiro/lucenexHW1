package lucenex;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymMap.Parser;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;




public class HomeWork1{
	
	public static void main (String args[]) throws IOException, ParseException {
		
		File folder = new File("FileTxt/");
		
		Path path = Paths.get("targetHW1/");
		Directory directory = FSDirectory.open(path);	
		
		
		CharArraySet stopWords = new CharArraySet(
				Arrays.asList("di","a","da","in","con","su","per","tra","fra","dei","del","delle","della","che","cui","e","a","i","gli","il","la","o","alla","alle","allo","al","ai","ci"), true);
		
		
		Analyzer a = CustomAnalyzer.builder()
		.withTokenizer(WhitespaceTokenizerFactory.class)
		.addTokenFilter(LowerCaseFilterFactory.class)
		.addTokenFilter(WordDelimiterGraphFilterFactory.class)
		.build();
		
		
		Map<String, Analyzer> perFielAnalyzers = new HashMap<>();
		perFielAnalyzers.put("Nome File", new WhitespaceAnalyzer());
		perFielAnalyzers.put("Contenuto", new ItalianAnalyzer());
		perFielAnalyzers.put("Contenuto", a);
		perFielAnalyzers.put("Contenuto", new StandardAnalyzer(stopWords));
		
		
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new ItalianAnalyzer(), perFielAnalyzers);
	
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCodec(new SimpleTextCodec());
		
		
		IndexWriter writer = new IndexWriter(directory,config);
		writer.deleteAll();
	
		
		
		for(File f : findAllFilesInFolder(folder)) {
		
			String content = new String(Files.readAllBytes(Paths.get(f.getPath())), StandardCharsets.ISO_8859_1);
			//System.out.println(f.getName().replace(".txt", ""));
			//System.out.println(content);
			Document d1 = new Document();
			Document d2 = new Document();
			d1.add(new TextField("Nome File", f.getName().replace(".txt", ""), Field.Store.YES));
			d2.add(new TextField("Contenuto", content, Field.Store.YES));
			writer.addDocument(d1);
			writer.addDocument(d2);
			
		}
		
		writer.commit();

		writer.close();

		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		
		
		System.out.println("Benvenuto in Lucene, cosa vuoi cercare? Digita:");
		System.out.println("Nome File -> per cercare il nome del File");
		System.out.println("Contenuto -> per cercare il Contenuto dei file");
		String search;
		
		Scanner scanIn = new Scanner(System.in);
		search = scanIn.nextLine();
		
		scanIn.close();
		
		QueryParser queryParser = new QueryParser(search, new WhitespaceAnalyzer());
		Query q = queryParser.parse("Il");
		
				
				
				
				
		//Query allDoc = new MatchAllDocsQuery();
		
		TopDocs hits = searcher.search(q, 10);
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("doc"+scoreDoc.doc + ":"+ doc.get("titolo") + " (" + scoreDoc.score +")");
            
            Explanation explanation = searcher.explain(q, scoreDoc.doc);
            System.out.println(explanation);
            
        }

		
		
		
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




