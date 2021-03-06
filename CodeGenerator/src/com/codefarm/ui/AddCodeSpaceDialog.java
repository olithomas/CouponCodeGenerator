package com.codefarm.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;

import com.codefarm.engine.CodeFormatter;
import com.codefarm.entities.FrontEndContext;
import com.codefarm.entities.User;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AddCodeSpaceDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel _contentPanel = new JPanel();
	private JCheckBox _chckbxExcludes;
	private JCheckBox _chckbxUserOwned;
	private JTextField _excludesText;
	private JTextField _codeSpaceNameText;
	private JComboBox<Object> _codeSpaceOwnerCombo;
	private JComboBox<Object> _codeLenCombo;
	private JLabel _lblCodeSpaceOwner;
	private JLabel _lblExcludes;
	private FrontEndContext _frontEnd;
	private boolean _isNewEntry;
	private JLabel _lblAdded;

	/**
	 * Create the dialog.
	 */
	public AddCodeSpaceDialog(FrontEndContext frontEnd) {
		_isNewEntry = false;
		_frontEnd = frontEnd;
		initialise();
	}
	
	private void addCodeSpace() {
		if ((_chckbxExcludes.isSelected() && _excludesText.getText().isEmpty()) || 
				_codeSpaceNameText.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "All fields must be specified.", "Add Code Space(s)", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (_chckbxExcludes.isSelected()) {
			if (_chckbxUserOwned.isSelected()) {
				// Excludes and User-Owned
				_frontEnd.addCodeSpace((Integer) _codeLenCombo.getSelectedItem(), 
						_excludesText.getText().toCharArray(), _codeSpaceNameText.getText(),
						((User) _codeSpaceOwnerCombo.getSelectedItem()).getUserId()); 
			} else {
				// Excludes only
				_frontEnd.addCodeSpace((Integer) _codeLenCombo.getSelectedItem(), 
						_excludesText.getText().toCharArray(), _codeSpaceNameText.getText());
			}
		} else {
			if (_chckbxUserOwned.isSelected()) {
				// No Excludes and User-Owned
				_frontEnd.addCodeSpace((Integer) _codeLenCombo.getSelectedItem(), 
						_codeSpaceNameText.getText(),
						((User) _codeSpaceOwnerCombo.getSelectedItem()).getUserId()); 
			} else {
				// Neither any excludes or user-owned
				_frontEnd.addCodeSpace((Integer) _codeLenCombo.getSelectedItem(), 
						_codeSpaceNameText.getText()); 
			}
		}
		_lblAdded.setVisible(true);
		_isNewEntry = true;
		_excludesText.setText("");
		_codeSpaceNameText.setText("");
		_codeSpaceNameText.requestFocus();
	}
	
	private void initialise() {
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Add Code Space(s)");
		setBounds(100, 100, 450, 332);
		getContentPane().setLayout(new BorderLayout());
		_contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(_contentPanel, BorderLayout.CENTER);
		
		ActionListener actionListener = new DialogActionListener();
		KeyAdapter keyListener = new TextBoxListener();
		
		_chckbxUserOwned = new JCheckBox("Is User-Owned (Default is Front End Owned)");
		_chckbxUserOwned.setActionCommand("userCheck");
		if (_frontEnd.getUsers().size() == 0) _chckbxUserOwned.setEnabled(false);
		_chckbxUserOwned.addActionListener(actionListener);
		_chckbxExcludes = new JCheckBox("Codes will Exclude Certain Characters (Specified Below)");
		_chckbxExcludes.setActionCommand("excludesCheck");
		_chckbxExcludes.addActionListener(actionListener);
		_lblCodeSpaceOwner = new JLabel("Code Space Owner:");
		_lblCodeSpaceOwner.setEnabled(false);
		_lblExcludes = new JLabel("Excluded Characters:");
		_lblExcludes.setEnabled(false);
		JLabel lblCodeStringLength = new JLabel("Code String Length:");
		JLabel lblCodeSpaceName = new JLabel("Code Space Name:");
		_codeSpaceOwnerCombo = new JComboBox<Object>();
		_codeSpaceOwnerCombo.setModel(new DefaultComboBoxModel<Object>(_frontEnd.getUsers().toArray()));
		_codeSpaceOwnerCombo.setEnabled(false);
		
		_excludesText = new JTextField();
		_excludesText.setEnabled(false);
		_excludesText.setColumns(10);
		_excludesText.addKeyListener(keyListener);
		
		_codeLenCombo = new JComboBox<Object>();
		ArrayList<Integer> codeLenArray = new ArrayList<Integer>();
		for (int i = CodeFormatter.MIN_CODE_LENGTH; i <= CodeFormatter.MAX_CODE_LENGTH; i++) {
			codeLenArray.add(i);
		}
		_codeLenCombo.setModel(new DefaultComboBoxModel<Object>(codeLenArray.toArray()));
		
		_codeSpaceNameText = new JTextField();
		_codeSpaceNameText.setColumns(10);
		_codeSpaceNameText.addKeyListener(keyListener);
		
		JLabel lblAddCodeSpace = new JLabel("Add the Code Space:");
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(actionListener);
		
		_lblAdded = new JLabel("Added!");
		_lblAdded.setVisible(false);
		GroupLayout gl_contentPanel = new GroupLayout(_contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(_chckbxUserOwned)
						.addComponent(_chckbxExcludes)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(lblCodeSpaceName, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblCodeStringLength, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(_lblCodeSpaceOwner, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(_lblExcludes, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(lblAddCodeSpace))
							.addGap(18)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(btnAdd)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(_lblAdded))
								.addComponent(_excludesText, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
								.addComponent(_codeSpaceOwnerCombo, 0, 293, Short.MAX_VALUE)
								.addComponent(_codeLenCombo, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
								.addComponent(_codeSpaceNameText, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(_chckbxUserOwned)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(_chckbxExcludes)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(_lblCodeSpaceOwner)
						.addComponent(_codeSpaceOwnerCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(_lblExcludes)
						.addComponent(_excludesText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(22)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCodeStringLength)
						.addComponent(_codeLenCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCodeSpaceName)
						.addComponent(_codeSpaceNameText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAddCodeSpace)
						.addComponent(btnAdd)
						.addComponent(_lblAdded))
					.addContainerGap())
		);
		_contentPanel.setLayout(gl_contentPanel);
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
				buttonPane.add(btnCancel);
			}
		}
	}
	
	private class DialogActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Add": 
				addCodeSpace();
				break;
			case "userCheck":
				_codeSpaceOwnerCombo.setEnabled(_chckbxUserOwned.isSelected());
				_lblCodeSpaceOwner.setEnabled(_chckbxUserOwned.isSelected());
				break;
			case "excludesCheck":
				_excludesText.setEnabled(_chckbxExcludes.isSelected());
				_lblExcludes.setEnabled(_chckbxExcludes.isSelected());
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
		public void keyPressed(KeyEvent arg0) {
			if (_isNewEntry) {
				_lblAdded.setVisible(false);
				_isNewEntry = false;
			}
		}
	}
}
