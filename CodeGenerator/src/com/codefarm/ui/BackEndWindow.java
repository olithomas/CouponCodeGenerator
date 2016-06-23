package com.codefarm.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.codefarm.entities.BackEndContext;
import com.codefarm.entities.FrontEndContext;

public class BackEndWindow extends JFrame implements Observer {
	
	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String BACKEND_PASSWORD = "backend1";
	public static final String BACKEND_IP = "172.21.16.17";
	
	private BackEndContext _backEnd;
	private JPanel _contentPane;
	private JTable _frontEndTable;
	private DefaultTableModel _frontEndTableModel;
	private Hashtable<String, FrontEndWindow> _frontEndWindows;
	private JButton _btnDeleteFrontEnd;

	public BackEndWindow(String ipAddress, String password) {
		_backEnd = new BackEndContext(ipAddress, password);
		_backEnd.addObserver(this);
		_frontEndWindows = new Hashtable<String, FrontEndWindow>();
		initialise();
	}
	
	/**
	 * Create a new front end JFrame. The actual Front End object will be created during registration when the user 
	 * does that through the front end JFrame
	 */
	private void createFrontEndWindow() {
		String frontEndIp = JOptionPane.showInputDialog(
				this, "Enter the Front End Window IP", "Open new Front End Window", JOptionPane.QUESTION_MESSAGE
				);
		if (frontEndIp == null) return;
		FrontEndWindow few = new FrontEndWindow(_backEnd, frontEndIp);
		_frontEndWindows.put(frontEndIp, few);
		few.setVisible(true);
	}
	
	private void refresh() {
		int rowCount = _frontEndTableModel.getRowCount();
		for (int i = rowCount; i > 0; i--) {
			_frontEndTableModel.removeRow(i - 1);
		}
		
		for (FrontEndContext fe : _backEnd.getFrontEndContexts()) {
			_btnDeleteFrontEnd.setEnabled(true);
			Object[] data = new Object[] {
					fe.getFrontEndId(), fe.getName(), fe.getFrontEndIp(), fe.getCreated(), 
					fe.getUsers().size(), fe.getCodeSpaces().size()
			};
			_frontEndTableModel.addRow(data);
		}
	}
	
	private int getActiveFrontEndFromTable() {
		int frontEndId;
		int row;
		if ((row = this._frontEndTable.getSelectedRow()) == -1) {
			return -1;
		} else {
			frontEndId = (Integer) this._frontEndTableModel.getValueAt(row, 0);
			return frontEndId;
		}
	}
	
	private FrontEndWindow getActiveFrontEndWindowFromTable() {
		FrontEndWindow few;
		int row;
		String feWindowIP = "";
		if ((row = this._frontEndTable.getSelectedRow()) == -1) {
			return null;
		} else {
			feWindowIP = (String) this._frontEndTableModel.getValueAt(row, 2);
			few = _frontEndWindows.get(feWindowIP);
			return few;
		}
	}
	
	@SuppressWarnings("serial")
	private void initialise() {
		
		ActionListener actionListener = new MyButtonListener();
		
		this.setTitle("Back End - " + _backEnd.getIpAddress());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 657, 409);
		_contentPane = new JPanel();
		_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(_contentPane);
		
		JLabel lblFrontEnds = new JLabel("Front Ends:");
		
		_frontEndTable = new JTable();
		_frontEndTableModel = new DefaultTableModel(
				new Object[0][0],
				new String[] { "ID", "Name", "IP Address", "Created", "Users", "Code Spaces" }) 
				{
					@SuppressWarnings("rawtypes")
					Class[] columnTypes = new Class[] {
						Integer.class, String.class, String.class, String.class, Integer.class, Integer.class
					};
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
					boolean[] columnEditables = new boolean[] {
						false, false, false, false, false, false
					};
					public boolean isCellEditable(int row, int column) {
						return columnEditables[column];
					}
				};
		_frontEndTable.setModel(_frontEndTableModel);

		_frontEndTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		_frontEndTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		_frontEndTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					getActiveFrontEndWindowFromTable().setVisible(true);
				}
			}
		});
		
		JScrollPane feTablePane = new JScrollPane(_frontEndTable);
		
		JButton btnAddFrontEnd = new JButton("Add Front End");
		btnAddFrontEnd.setActionCommand("Add");
		btnAddFrontEnd.addActionListener(actionListener);
		
		_btnDeleteFrontEnd = new JButton("Delete Front End");
		_btnDeleteFrontEnd.setEnabled(false);
		_btnDeleteFrontEnd.setActionCommand("Delete");
		_btnDeleteFrontEnd.addActionListener(actionListener);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(actionListener);
		
		JButton btnTestRandom = new JButton("Test Random");
		btnTestRandom.addActionListener(actionListener);
		btnTestRandom.setActionCommand("Test");
		GroupLayout gl_contentPane = new GroupLayout(_contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(feTablePane, GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
						.addComponent(lblFrontEnds)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnAddFrontEnd)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_btnDeleteFrontEnd)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRefresh)
							.addPreferredGap(ComponentPlacement.RELATED, 225, Short.MAX_VALUE)
							.addComponent(btnTestRandom)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblFrontEnds)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(feTablePane, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAddFrontEnd)
						.addComponent(_btnDeleteFrontEnd)
						.addComponent(btnRefresh)
						.addComponent(btnTestRandom))
					.addContainerGap())
		);
		_contentPane.setLayout(gl_contentPane);
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable()
	     {
	         public void run()
	         {
	        	 /*
	        	  * Back end window IP and password defined in constants in this class
	        	  */
	             //create GUI frame
	        	 new BackEndWindow(BackEndWindow.BACKEND_IP, BackEndWindow.BACKEND_PASSWORD).setVisible(true);          
	         }
	     });
	}
	
	private class MyButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Add":
				createFrontEndWindow();
				break;
			case "Delete":
				int frontEndId = getActiveFrontEndFromTable();
				_backEnd.deRegister(frontEndId);
				getActiveFrontEndWindowFromTable().dispose();
				break;
			case "Refresh":
				refresh();
				break;
			case "Test":
				RandomTester testFrame = new RandomTester();
				testFrame.pack();
		        testFrame.setVisible(true);
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		refresh();
	}
}
