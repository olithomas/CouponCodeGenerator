package com.codefarm.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;

import com.codefarm.entities.CodeSpace;
import com.codefarm.entities.FrontEndContext;
import com.codefarm.entities.User;

public class AddCodesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField _codeCountText;
	private JLabel _lblAdded;
	private boolean _isNewEntry;
	private FrontEndContext _frontEnd;
	private JComboBox<Object> _userCombo;
	private JComboBox<Object> _codeSpaceCombo;
	
	/**
	 * Create the dialog.
	 */
	public AddCodesDialog(FrontEndContext frontEnd) {
		_isNewEntry = false;
		_frontEnd = frontEnd;
		initialise();
	}
	
	private void addCodes() {
		if (this._codeCountText.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "All fields must be specified.", "Add Code(s)", JOptionPane.ERROR_MESSAGE);
			return;
		}
		int codeSpaceId = ((CodeSpace) _codeSpaceCombo.getSelectedItem()).getCodeSpaceId();
		int userId = ((User) _userCombo.getSelectedItem()).getUserId();
		_frontEnd.addCodes(Integer.valueOf(_codeCountText.getText()), codeSpaceId, userId);
		_lblAdded.setVisible(true);
		_isNewEntry = true;
		_codeCountText.setText("");
		_codeCountText.requestFocus();
	}
	
	private void initialise() {
		setTitle("Add Code(s)");
		setResizable(false);
		setBounds(100, 100, 347, 234);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		ActionListener actionListener = new DialogActionListener();
		KeyAdapter keyAdapter = new TextBoxListener();
		
		JLabel lblUser = new JLabel("User:");
		
		JLabel lblCodeSpace = new JLabel("Code Space:");
		
		JLabel lblNumberOfCodes = new JLabel("Number of Codes:");
		
		JLabel lblAddCodes = new JLabel("Add Codes:");
		
		Object[] users = _frontEnd.getUsers().toArray();
		Object[] codeSpaces = _frontEnd.getMyCodeSpaces().toArray();
		
		_userCombo = new JComboBox<Object>(new DefaultComboBoxModel<Object>(users));
		_userCombo.addKeyListener(keyAdapter);
		
		_codeSpaceCombo = new JComboBox<Object>(new DefaultComboBoxModel<Object>(codeSpaces));
		_codeSpaceCombo.addKeyListener(keyAdapter);
		
		_codeCountText = new JTextField();
		_codeCountText.setColumns(10);
		_codeCountText.addKeyListener(keyAdapter);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(actionListener);
		
		_lblAdded = new JLabel("Added!");
		_lblAdded.setVisible(false);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblNumberOfCodes, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblAddCodes, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblCodeSpace, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(_codeSpaceCombo, 0, 307, Short.MAX_VALUE)
						.addComponent(_userCombo, 0, 307, Short.MAX_VALUE)
						.addComponent(_codeCountText, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(btnAdd)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_lblAdded)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUser)
						.addComponent(_userCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCodeSpace)
						.addComponent(_codeSpaceCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNumberOfCodes)
						.addComponent(_codeCountText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAddCodes)
						.addComponent(btnAdd)
						.addComponent(_lblAdded))
					.addContainerGap(98, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnDone = new JButton("Done");
				btnDone.addActionListener(actionListener);
				buttonPane.add(btnDone);
				getRootPane().setDefaultButton(btnAdd);
			}
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(actionListener);
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
		}
	}
	
	private class DialogActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Add": 
				addCodes();
				break;
			default: // All other buttons just dispose
				setVisible(false);
				dispose();
				break;
			}
		}
	}
	
	private class TextBoxListener extends KeyAdapter {
		@Override
		public void keyTyped(KeyEvent arg0) {
			if (_isNewEntry) {
				_lblAdded.setVisible(false);
				_isNewEntry = false;
			}
		}
	}
}
