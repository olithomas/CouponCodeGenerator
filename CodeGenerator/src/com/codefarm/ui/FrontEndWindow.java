package com.codefarm.ui;

import com.codefarm.entities.*;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Observer;
import java.util.Observable;

import javax.swing.JTabbedPane;

public class FrontEndWindow extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Font DEFAULT_TABLE_FONT = new Font("Courier New", Font.PLAIN, 12);
	
	private JPanel _contentPane;
	private BackEndContext _backEnd;
	private BackEndContext.TemporaryContext _tempContext;
	private FrontEndContext _frontEndContext;
	private String _frontEndIp;
	private boolean _connected;
	private boolean _authenticated;
	private JMenuItem _mntmConnectToBack;
	private JMenuItem _mntmAuthenticate;
	private JMenuItem _mntmRegisterFrontEnd;
	private JMenuItem _mntmCsRegister;
	private JTabbedPane _tabbedPane;
	private UserTab _userTab; // is a JPanel;
	

	public String getFrontEndIp() { return _frontEndIp; }
	
	/**
	 * Create the frame.
	 */
	public FrontEndWindow(BackEndContext backEnd, String frontEndIP) {
		_connected = false;
		_authenticated = false;
		_frontEndIp = frontEndIP;
		_backEnd = backEnd;
		initialise();
	}
	
	private void connect() {
		String backEndIP;
		while (!_connected) {
			backEndIP = JOptionPane.showInputDialog(
					this, "Enter the IP address of the back end:", "Connect Front End", JOptionPane.QUESTION_MESSAGE
					);
			if (backEndIP == null) break;
			_connected = _backEnd.getIpAddress().equals(backEndIP);
			if (!_connected) {
				JOptionPane.showMessageDialog(this, "Incorrect IP address", "Please try again",JOptionPane.ERROR_MESSAGE);
			} else {
				_tempContext = _backEnd.connect(_frontEndIp);
				this.setTitle(this.getTitle() + " (Connected)");
				_mntmConnectToBack.setEnabled(false);
				_mntmAuthenticate.setEnabled(true);
			}
		}
	}
	
	private void authenticate() { // Must only be called after connect() otherwise _tempContext is not initialised..
		if (!_connected) return;
		String password;
		while (!_authenticated) {
			password = JOptionPane.showInputDialog(
					this, "Enter the back end password: (Hint: '" + BackEndWindow.BACKEND_PASSWORD + "')", 
					"Authenticate Front End", 
					JOptionPane.QUESTION_MESSAGE
					);
			if (password == null) break;
			_authenticated = _backEnd.authenticate(_tempContext, password);
			if (!_authenticated) {
				JOptionPane.showMessageDialog(this, "Incorrect Password", "Please try again", JOptionPane.ERROR_MESSAGE);
			} else {
				this.setTitle(this.getTitle() + " (authenticated)");
				_mntmConnectToBack.setEnabled(false);
				_mntmAuthenticate.setEnabled(false);
				_mntmRegisterFrontEnd.setEnabled(true);
			}
		}
	}
	
	private void register() { // Must only be called after connect() and authenticate()..
		if (!_connected || !_authenticated) return;
		String frontEndName = JOptionPane.showInputDialog(
				this, "Enter the Front End Name", "Register Front End", JOptionPane.QUESTION_MESSAGE
				);
		if (frontEndName == null) return;
		_frontEndContext = _backEnd.register(_tempContext, frontEndName);
		_frontEndContext.addObserver(this);
		// Create the user tab
		_userTab = new UserTab(_frontEndContext);
		// and add it to the tabbed pane
		_tabbedPane.addTab("Users", _userTab);
		// Sort out the control properties..
		_mntmRegisterFrontEnd.setEnabled(false);
		_mntmAuthenticate.setEnabled(false);
		this.setTitle("Front End: " + frontEndName + " (Registered)");
		_mntmCsRegister.setEnabled(true);
	}

	private void initialise() {
		
		ActionListener actionListener = new MyActionListener();
		
		this.setTitle("Front End Window: - " + _frontEndIp);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 784, 592);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu _menuFrontEnds = new JMenu("Manage Front End");
		menuBar.add(_menuFrontEnds);
		
		_mntmConnectToBack = new JMenuItem("Connect to Back End");
		_mntmConnectToBack.setActionCommand("connect");
		_mntmConnectToBack.addActionListener(actionListener);
		_menuFrontEnds.add(_mntmConnectToBack);
		
		_mntmAuthenticate = new JMenuItem("Authenticate Front End");
		_mntmAuthenticate.setActionCommand("authenticate");
		_mntmAuthenticate.setEnabled(false);
		_mntmAuthenticate.addActionListener(actionListener);
		_menuFrontEnds.add(_mntmAuthenticate);
		
		_mntmRegisterFrontEnd = new JMenuItem("Register Front End");
		_mntmRegisterFrontEnd.setActionCommand("register");
		_mntmRegisterFrontEnd.addActionListener(actionListener);
		_mntmRegisterFrontEnd.setEnabled(false);
		_menuFrontEnds.add(_mntmRegisterFrontEnd);
		
		_mntmCsRegister = new JMenuItem("Register Code Space");
		_mntmCsRegister.setActionCommand("registerCs");
		_mntmCsRegister.addActionListener(actionListener);
		_mntmCsRegister.setEnabled(false);
		_menuFrontEnds.add(_mntmCsRegister);
		_contentPane = new JPanel();
		_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		_contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(_contentPane);
		
		
		_tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		_contentPane.add(_tabbedPane, BorderLayout.CENTER);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg == null) {
			return;
		} else if (arg.getClass().equals(CodeSpace.class)) {
			CodeSpace cs = (CodeSpace) arg;
			CodeSpaceTab cst = new CodeSpaceTab(_frontEndContext, cs.getCodeSpaceId());
			String name;
			if (cs.getOwnerType() == Code.USER_TYPE) {
				name = cs.getCodeSpaceName() + " (" + _frontEndContext.getUser(cs.getOwnerId()).toString() + ")";
			} else {
				name = cs.getCodeSpaceName();
			}
			this._tabbedPane.add(name, cst);
		}
	}

	public boolean isConnected() {
		return _connected;
	}

	public boolean isAuthenticated() {
		return _authenticated;
	}
	
	private class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			switch (arg0.getActionCommand()) {
			case "connect":
				connect();
				break;
			case "authenticate":
				authenticate();
				break;
			case "register":
				register();
				break;
			case "registerCs":
				AddCodeSpaceDialog acsd = new AddCodeSpaceDialog(_frontEndContext);
				acsd.setLocationRelativeTo(FrontEndWindow.this);
				acsd.setVisible(true);
				break;
			default:
				// do nothing
				break;
			}
		}
	}
}
