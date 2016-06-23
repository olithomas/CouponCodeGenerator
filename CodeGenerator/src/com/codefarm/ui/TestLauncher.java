package com.codefarm.ui;

import java.awt.BorderLayout;
import java.math.BigInteger;

import javax.swing.JFrame;

import com.codefarm.entities.Code;
import com.codefarm.entities.CodeSpace;
import com.codefarm.entities.FrontEndContext;
import com.codefarm.entities.User;

public class TestLauncher {
	
	Code code;
	FrontEndContext fe;
	CodeSpace cs;
	private User testUser;

	public Code getCode() {
		return code;
	}

	public TestLauncher() {
		code = new Code(BigInteger.TEN, "000000000A");
		code.setOwner(10, Code.USER_TYPE);
		code.setCodeData("This is a test!");
		fe = new FrontEndContext("Test FE");
		int csid = fe.addCodeSpace(10, "Test Code Space");
		cs = fe.getCodeSpace(csid);
		fe.addCodes(10, csid);
		int uid = fe.addUser(new String[] { "Oli", "Thomas", "oli@email.com" });
		testUser = fe.getUser(uid);
		fe.addCodes(10, csid, uid);
	}

	public static void main(String[] args) {
		TestLauncher tl = new TestLauncher();
		// tl.codeInfoPanelDisplay();
		// tl.codeSpaceTabDisplay();
		// tl.setCodeDataDialogDisplay();
		// tl.addCodeSpaceDialogDisplay();
		tl.addUserDisplay();
	}
	
	public void codeInfoPanelDisplay() {
		CodeInfoPanel cip = new CodeInfoPanel();
		cip.refreshCodeData(code);
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(cip, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void codeSpaceTabDisplay() {
		CodeSpaceTab cst = new CodeSpaceTab(fe, cs.getCodeSpaceId());
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(cst, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setCodeDataDialogDisplay() {
		SetCodeDataDialog scd = new SetCodeDataDialog(testUser.getUserId(), Code.USER_TYPE, fe);
		scd.setVisible(true);
	}
	
	public void addCodeSpaceDialogDisplay() {
		AddCodeSpaceDialog acsd = new AddCodeSpaceDialog(fe);
		acsd.setVisible(true);
	}

	public void addUserDisplay() {
		new AddUserDialog(fe).setVisible(true);
	}
}
