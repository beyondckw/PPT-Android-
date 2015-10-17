package com.newppt.android.ui;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.batik.swing.JSVGCanvas;

import com.newppt.android.data.AndroidServer;
import com.newppt.android.logical.NetWorkInfo;
import com.newppt.android.logical.Svg;

public class AndroidPPT extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected MyButton2 _pre, _next, _big, _small, _load, _first, _last, _broadcastIP, _exit;
	JLabel _label;  
	// private JComboBox broadcastIP;

	protected JPanel _p1, _p3;    //p1Ϊ�ұߵ���壬p3Ϊ��ߵ����
	protected JSVGCanvas _svgCanvas = new JSVGCanvas();
	protected FileDialog _fileDialog = new FileDialog(this);
	protected float higth, h;
	protected float width, w;
	protected final int _iniHeight = 850, _iniWidth = 550;
	public int _currentPage, _pages;
	public String _Path;
	public String _fileName;
	private Svg _svg;
	public volatile boolean _notify = false;			//���һ���ֶα�������volatile��java�߳��ڴ�ģ��ȷ�������߳̿������������ֵ��һ�µġ�
	private volatile boolean _breakTip = false;
	private String _ipString;
	private String _broadcastAddress;
	
	IPDialog dialog;
	
	public boolean flag = false; //���������Ƿ�Ҫ���ذ�ť��false��������
	//IPDialog dialog;

	public AndroidPPT(String title) {

		super(title);
	//	this.setUndecorated(true);  //�ޱ�������
		
		ImageIcon image = new ImageIcon("ͼƬ.jpg");     //������������ʾһ��ͼƬ
        _label = new JLabel();
        _label.setIcon(image);
        this.add(_label, "Center");
		
		_notify = false;
		layoutFrame();
		ButtonEvent();
		h = higth = this._svgCanvas.getSize().height;
		w = width = this._svgCanvas.getSize().width;
		windowsListerner();

	}

	protected void layoutFrame() {
		this.setSize(_iniHeight, _iniWidth);
		_broadcastIP = new MyButton2("broadcast");
		// broadcastIP = new JComboBox();
		_load = new MyButton2("load");
		_big = new MyButton2("big");
		_small = new MyButton2("small");
		_pre = new MyButton2("Prior");
		_next = new MyButton2("Next");
		_first = new MyButton2("First");
		_last = new MyButton2("Last");
		_exit = new MyButton2("Exit");
		
		_load.setFocusable(false);						//ȥ����ť�ı߿�
		_load.setBorderPainted(false);
		_broadcastIP.setFocusable(false);						
		_broadcastIP.setBorderPainted(false);
		_big.setFocusable(false);						
		_big.setBorderPainted(false);
		_small.setFocusable(false);						
		_small.setBorderPainted(false);
		_pre.setFocusable(false);						
		_pre.setBorderPainted(false);
		_next.setFocusable(false);						
		_next.setBorderPainted(false);
		_first.setFocusable(false);						
		_first.setBorderPainted(false);
		_last.setFocusable(false);						
		_last.setBorderPainted(false);
		_exit.setFocusable(false);						
		_exit.setBorderPainted(false);
		
		_p1 = new JPanel();

		_p1.setVisible(false);		//��ʼ�ұߵ����Ϊ���ɼ�
	    _p1.setLayout(new GridLayout(9,1));

	    _p3 = new JPanel();    //���p3������Ϊ������ͼƬ�봰����ߵı߾࣬���е������Ҳ����ʵ�ֶ԰�ť������
		
		_p3.setBackground(Color.white);
		this.addMouseListener(new MouseAdapter() {    //������¼����������������Ӧ
 
            public void mouseClicked(MouseEvent e) {
            	if(flag == false){    //���°�ť������
            		flag = true;    
            		_p1.setVisible(false);
            	}else{					//���°�ť�ͳ���
            		flag = false;
            		_p1.setVisible(true);
            	}
                
            }
 
        });
		
		 MouseAdapter moveWindowListener = new MouseAdapter() {  
			  
	        Point lastPoint = null;  
	  
	        @Override  
	        public void mousePressed(MouseEvent e) {  
	            lastPoint = e.getLocationOnScreen();  
	        }  
	  
	        @Override  
	        public void mouseDragged(MouseEvent e) {  
	            Point point = e.getLocationOnScreen();  
	            int offsetX = point.x - lastPoint.x;  
	            int offsetY = point.y - lastPoint.y;  
	            Rectangle bounds = AndroidPPT.this.getBounds();  
	            bounds.x += offsetX;  
	            bounds.y += offsetY;  
	            AndroidPPT.this.setBounds(bounds);  
	            lastPoint = point;  
	        }  
	    };  
		this.addMouseListener(moveWindowListener);    //���ԶԴ�������϶�
		this.addMouseMotionListener(moveWindowListener);
		
		this.setFocusable(true);   //�趨�������ڴ��ڣ���仰����Ҫ����Ȼ�����¼�����������
		
		/**
		 * ���Ӽ����¼�
		 */
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e){
				
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {//���̰��������¼�  
				  // TODO Auto-generated method stub  
				  switch(e.getKeyCode())  
				  {  
				  case KeyEvent.VK_RIGHT:  
				    
				   break;  
				  case KeyEvent.VK_LEFT:  
				     
				   break;  
				  case KeyEvent.VK_UP:  
					  if(_currentPage == 0)
			        		_pre.setEnabled(false);
			        	else{
			        		if(_currentPage-- == _pages-1)		
			        			_next.setEnabled(true);		
			        	}
			        	
					    changeSize();
			        	showPPT();
				   break;  
				  case KeyEvent.VK_DOWN:  
					  if(_currentPage == _pages-1)
			        		_next.setEnabled(false);
			        	else{
			        		if(_currentPage++ == 0) 
			        			_pre.setEnabled(true);        	
			        	}
					    changeSize();
			        	showPPT();
			        	
				   break;  
				  case KeyEvent.VK_SPACE:  
					  if(flag == false){    //���¿ո�������ϵİ�ť������
		            		flag = true;    
		            		_p1.setVisible(false);
		            	}else{					//���¿ո�������ϵİ�ť�ͳ���
		            		flag = false;
		            		_p1.setVisible(true);
		            	}
					  break;
				   default:  
				        
				  }  
				  
				    
				 }  
		});

		_p1.add(_broadcastIP);
		_p1.add(_load);
		_p1.add(_big);
		_p1.add(_small);
		_p1.add(_pre);
		_p1.add(_next);
		_p1.add(_first);
		_p1.add(_last);
		_p1.add(_exit);

		this.add(_p3, "West");
		//	this.add(svgCanvas, "Center"); 						//�Ȱ�svgCanvas���ǵ�
		this.add(_p1, "East");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		centerWindow();
		this.setVisible(true);
	}

	/**
	 * ���ھ���
	 */
	protected void centerWindow() {
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		setLocation((screenWidth - getWidth()) / 2, (screenHeight - getHeight()) / 2);
	}

	/**
	 * ��ȡ·��
	 */
	private void getPath() {
		FileDialog d = new FileDialog(this);
		d.setVisible(true);
		String s = d.getDirectory();
		_fileName = d.getFile();
		System.out.println("1111111" + _fileName);
		System.out.println(s + _fileName);
		_Path = s + _fileName;
	}

	/**
	 * ��ť�¼�����
	 */
	protected void ButtonEvent() {
		_pre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_notify = false;
				if (_currentPage <= 1) {
					_pre.setEnabled(false);
					return;
				} else {
					if (_currentPage-- == _pages)
						_next.setEnabled(true);
				}
				changeSize();
				showPPT();
				// notify = true;
			}

		});

		_first.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_notify = false;
				if (_currentPage == 1)
					return;
				_currentPage = 1;
				changeSize();
				showPPT();
				// notify = true;
			}

		});

		_last.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_notify = false;
				if (_currentPage == _pages)
					return;
				_currentPage = _pages;
				changeSize();
				showPPT();
				// notify = true;
			}

		});

		_next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_notify = false;
				if (_currentPage == _pages) {
					_next.setEnabled(false);
					return;
				} else {
					if (_currentPage++ == 1)
						_pre.setEnabled(true);
				}
				// change();
				showPPT();
				// notify = true;
			}
		});

		_exit.addActionListener(new ActionListener(){    //���������ť֮����˳�
			 public void actionPerformed(ActionEvent e) {
				 System.exit(0);
			 }
	     });
		
		_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getPath();
				_notify = false;

				_svg = new Svg(_fileName, _Path);
				_pages = _svg.page;
				_currentPage = 1;
				changeSize();
				showPPT();
			}

		});

		_big.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.awt.geom.AffineTransform rat = _svgCanvas
						.getRenderingTransform();
				rat.scale(1.5, 1.5);
				_svgCanvas.setRenderingTransform(rat);
				higth *= 1.5;
				width *= 1.5;
			}
		});

		_small.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.awt.geom.AffineTransform rat = _svgCanvas
						.getRenderingTransform();
				rat.scale(0.5, 0.5);
				_svgCanvas.setRenderingTransform(rat);
				higth *= 0.5;
				width *= 0.5;
			}
		});

		_broadcastIP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_breakTip = true;

				dialog = new IPDialog(AndroidPPT.this, "��ȡIP", true);
				
				if ((_ipString = dialog.getIp()) == null) {
					return;
				}
				/***
				 * ����wifi��ip��ַ,���ڼ���������ϵĹ㲥��ַ
				 */
			/*	if ((_ipString = dialog.getAllLocalHostIP()[1]) == null) {
					return;
				}					
				*/
				
				// System.out.println("0000  "+ipString);
				// ipString = JOptionPane.showInputDialog("input IP");
				// subnetString = JOptionPane.showInputDialog("input ��������");
				NetWorkInfo netWorkInfo = new NetWorkInfo(_ipString);
				_broadcastAddress = netWorkInfo.getBroadcastAddress();
				System.out.println("����"+_broadcastAddress);

				new Thread() {
					public void run() {
						_breakTip = false;
						while (true) {		//ÿ��3��͹㲥һ��IP��ַ
							try {
								sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							sendIP();
							if (_breakTip)
								break;

						}

					}

					private void sendIP() {
						try {
							DatagramSocket dgSocket = new DatagramSocket(8989);
							/***
							 * _ipString����������ip��ַ�����ڷ��͵��ͻ�������
							 */
							//_ipString = dialog.getAllLocalHostIP()[0];
							byte[] by = _ipString.getBytes();
							System.out.println("1");
							try {
								DatagramPacket packet = new DatagramPacket(by,by.length, InetAddress.getByName(_broadcastAddress),8989);
								try {
									System.out.println("11");
									dgSocket.send(packet);
									System.out.println("22");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								dgSocket.close();
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
			}
		});
	}

	public void windowsListerner() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// System.out.println("dfsfaaaa");
				changeSize();
			}
		});

	}

	protected void changeSize() {
		java.awt.geom.AffineTransform rat = _svgCanvas.getRenderingTransform();
		float a = this._svgCanvas.getSize().height / higth;
		float b = this._svgCanvas.getSize().width / width;
		rat.scale(b, a);
		width = this._svgCanvas.getSize().width;
		higth = this._svgCanvas.getSize().height;
		_svgCanvas.setRenderingTransform(rat);

	}

	protected void resizeSVG() {
		java.awt.geom.AffineTransform at = _svgCanvas.getRenderingTransform();  
		float c = this._svgCanvas.getSize().height;
		float d = this._svgCanvas.getSize().width;
		System.out.println(h + " " + w + " " + c + " " + d);
		float a = c / h;
		float b = d / w;
		System.out.println(a + " " + b);
		at.scale(b, a);
		_svgCanvas.setRenderingTransform(at);
	}

	/**
	 * show PPT
	 */
	protected void showPPT() {
		
		_label.setVisible(false);			//��ʼչʾPPT������ҳ���ͼƬ���ǵ�
	    this.add(_svgCanvas, "Center");
	    
		_svgCanvas.setDocument(_svg.getAndSetDoc(_currentPage));
		_notify = true;

		System.out.println("����" + _pages + " ҳ");
		int currentPages = _currentPage;
		System.out.println("Ŀǰ" + currentPages + " ҳ");
	}

	public static void main(String[] args) throws Exception {
		new AndroidServer();
	}
	
	public byte[] getImageBytes()
	{
		return _svg.getImageBytes();
	}
	
	public byte[] getFileBytes() {
		return _svg.getFileBytes();
	}
}

/****
 * ������Բ�εİ�ť	
 * @author Administrator
 *
 */
	
class MyButton2 extends JButton { 
	private static final long serialVersionUID = 1965063150601339314L;
	 
    public MyButton2(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false); // ��һ��ǳ���Ҫ, �����໹����ư�ť������.
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
 
        Graphics2D g2d = (Graphics2D) g;
 
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
 
        g2d.setColor(Color.GRAY);
        g2d.fillRoundRect(0, 0, width, height, 50, 50);
 
        super.paintComponent(g); // �������������, �ø�������ڰ�ť�ϻ�������.
    }

}
