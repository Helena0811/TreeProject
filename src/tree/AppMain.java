package tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.LyricsHandler;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;



public class AppMain extends JFrame implements TreeSelectionListener, Runnable{
	JPanel p_west, p_center;
	JTree tree;
	JScrollPane scroll;
	JTextArea area;
	
	DefaultMutableTreeNode root=null;
	
	String path="C:/java_workspace2/TreeProject/";
	
	String fileLocation;
	
	Thread thread;
	
	boolean flag=false;
	
	boolean isClicked=false;
	
	public AppMain() {
		p_west=new JPanel();
		p_center=new JPanel();
		
		// 최상위 노드 생성
		//root=new DefaultMutableTreeNode("과일");
		//createNodes(root);
		//createDirectory();
		createMusicDir();
		
		tree=new JTree(root);
		scroll=new JScrollPane(tree);
		area=new JTextArea();
		
		// tree와 TreeSelectionListener 연결
		tree.addTreeSelectionListener(this);
		
		p_west.setLayout(new BorderLayout());
		p_west.add(scroll);
		p_west.setPreferredSize(new Dimension(200, 500));
		//p_center.add(area);
		add(p_west, BorderLayout.WEST);
		//add(p_center);
		add(area);
		
		setSize(700, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/*
	// 자식 노드 생성
	public void createNodes(){
		root=new DefaultMutableTreeNode("과일");
		
		DefaultMutableTreeNode node1=null;
		DefaultMutableTreeNode node2=null;
		DefaultMutableTreeNode node3=null;
		
		node1=new DefaultMutableTreeNode("레몬");
		node2=new DefaultMutableTreeNode("블루베리");
		node3=new DefaultMutableTreeNode("수입산 과일");
		
		root.add(node1);
		root.add(node2);
		root.add(node3);
		
		DefaultMutableTreeNode node4=null;
		DefaultMutableTreeNode node5=null;
		
		node4=new DefaultMutableTreeNode("아보카도");
		node5=new DefaultMutableTreeNode("바나나");
		
		node3.add(node4);
		node3.add(node5);
		
	}
	
	// 윈도우의 구조 보여주기(파일 탐색기)
	public void createDirectory(){
		root=new DefaultMutableTreeNode("내 pc");
		
		// 드라이브 파티션 출력, File[]반환됨
		File[] drive=File.listRoots();
		
		// 볼륨 드라이브를 볼 때 사용, 디스크 볼륨 정보 표현
		// abstract class이므로 생성자 X
		// method 중 반환형이 FileSystemView인 getFileSystemView() 사용
		FileSystemView fsv=FileSystemView.getFileSystemView();
		
		for(int i=0; i<drive.length; i++){
			// 파일 이름
			String volume=fsv.getSystemDisplayName(drive[i]);
			DefaultMutableTreeNode node=new DefaultMutableTreeNode(volume);
			
			root.add(node);
			
			File[] folder=drive[i].listFiles();
			
			System.out.println(folder);
			
			// 밑에 하위폴더 나오게 구현해보기 
			
			for(int j=0; j<folder.length; j++){
				DefaultMutableTreeNode subNode=new DefaultMutableTreeNode(folder[j]);
				node.add(subNode);
			}
		}
	}
	*/
	
	// mp3 재생 구현
	public void createMusicDir(){
		root=new DefaultMutableTreeNode("쥬크박스");

		File file=new File(path+"data");
		File[] child=file.listFiles();
		
		for(int i=0; i<child.length; i++){
			DefaultMutableTreeNode node=new DefaultMutableTreeNode(child[i].getName());
			root.add(node);
		}
	}
	
	// 선택한 노드 파일인 mp3 파일 정보 추출
	public String extract(String filename){
		//System.out.println(filename);
		
		fileLocation = path+"data/"+filename;
		
		StringBuffer sb=new StringBuffer();

		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = null;
		try {
			inputstream=new FileInputStream(new File(fileLocation));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
       ParseContext pcontext = new ParseContext();
       
       //Mp3 parser
       Mp3Parser  Mp3Parser = new  Mp3Parser();
       
       LyricsHandler lyrics;
       try {
    	   Mp3Parser.parse(inputstream, handler, metadata, pcontext);
    	   lyrics = new LyricsHandler(inputstream,handler);
    	   while(lyrics.hasLyrics()) {
    		   sb.append(lyrics.toString());
    		   System.out.println(lyrics.toString());
    	   }
       } catch (IOException e) {
    	   e.printStackTrace();
       } catch (SAXException e) {
    	   e.printStackTrace();
       } catch (TikaException e) {
    	   e.printStackTrace();
       }
	   sb.append("Contents of the document:" + handler.toString());
	   sb.append("Metadata of the document:");
	   System.out.println("Contents of the document:" + handler.toString());
	   System.out.println("Metadata of the document:");
	   String[] metadataNames = metadata.names();

	   for(String name : metadataNames) {          
		   sb.append(name + ": " + metadata.get(name)+"\n");
		   System.out.println(name + ": " + metadata.get(name));  
	   }
	   return sb.toString();
	}
	
	// Tree에서 node를 클릭하면
	public void valueChanged(TreeSelectionEvent e) {
		// 출력하기 전에 먼저 지우기
		area.setText("");
		// Tree가 이벤트를 일으킨 주체
		JTree tree=(JTree)e.getSource();
		
		// JTree.getLastSelectedPathComponent() -> node 반환
		// 선택된 파일 이름 출력
		DefaultMutableTreeNode node=(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		// 자체적으로 toString()이 수행되어 출력
		System.out.println(node.getUserObject()+" 클릭");
		//extract(node.getUserObject().toString());
		
		// textArea에 출력
		String content=extract(node.getUserObject().toString());
		area.append(content);

		flag=true;
		thread=new Thread(this);
		thread.start();
	}
	
	// 선택한 MP3 파일 재생 -> JLayer - MP3 Library 사용
	public void play(){
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(new File(fileLocation));
			AdvancedPlayer player=new AdvancedPlayer(fis);
			player.play();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		} finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void run() {
		while(flag){
			play();
		}
	}
	
	public static void main(String[] args) {
		new AppMain();
	
	}

}
