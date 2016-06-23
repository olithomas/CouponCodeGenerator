package com.codefarm.ui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.codefarm.entities.Code;

public class CodeInfoPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField _codeText;
	private JTextField _codeOwnerText;
	private JTextField _createdText;
	private JTextField _updateText;
	private JTextArea _codeDataText;

	/**
	 * Create the panel.
	 */
	public CodeInfoPanel() {
		initialise();
	}
	
	public CodeInfoPanel(Code code) {
		initialise();
		refreshCodeData(code);
	}

	private void initialise() {

		JLabel lblCode = new JLabel("Code:");
		
		_codeText = new JTextField();
		_codeText.setEditable(false);
		_codeText.setColumns(10);
		
		JLabel lblOwner = new JLabel("Owner:");
		
		_codeOwnerText = new JTextField();
		_codeOwnerText.setEditable(false);
		_codeOwnerText.setColumns(10);
		
		JLabel lblCreated = new JLabel("Created:");
		
		_createdText = new JTextField();
		_createdText.setEditable(false);
		_createdText.setColumns(10);
		
		JLabel lblLastUpdate = new JLabel("Last Update:");
		
		_updateText = new JTextField();
		_updateText.setEditable(false);
		_updateText.setColumns(10);
		
		JLabel lblData = new JLabel("Data:");
		
		_codeDataText = new JTextArea();
		_codeDataText.setEditable(false);
		_codeDataText.setColumns(10);
        
		GroupLayout gl__codeInfoPanel = new GroupLayout(this);
		gl__codeInfoPanel.setHorizontalGroup(
			gl__codeInfoPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl__codeInfoPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl__codeInfoPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(_codeDataText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
						.addComponent(lblData, Alignment.LEADING)
						.addGroup(Alignment.LEADING, gl__codeInfoPanel.createSequentialGroup()
							.addComponent(lblLastUpdate)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_updateText, GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, gl__codeInfoPanel.createSequentialGroup()
							.addGroup(gl__codeInfoPanel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblCode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblCreated, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl__codeInfoPanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl__codeInfoPanel.createSequentialGroup()
									.addComponent(_codeText, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
									.addGap(18)
									.addComponent(lblOwner)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(_codeOwnerText, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE))
								.addComponent(_createdText, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl__codeInfoPanel.setVerticalGroup(
			gl__codeInfoPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl__codeInfoPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl__codeInfoPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCode)
						.addComponent(_codeOwnerText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblOwner)
						.addComponent(_codeText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl__codeInfoPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCreated)
						.addComponent(_createdText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl__codeInfoPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblLastUpdate)
						.addComponent(_updateText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(19)
					.addComponent(lblData)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(_codeDataText, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		this.setLayout(gl__codeInfoPanel);
	}
	
	public void refreshCodeData(Code thisCode) {
		_codeText.setText(thisCode.getStringVal());
		_createdText.setText(thisCode.getCreationTimestamp().toString());
		_codeOwnerText.setText(String.valueOf(thisCode.getOwnerId()));
		Object[] codeData = new Object[2];
		try {
			codeData = thisCode.getCodeData();
		} catch (Exception e) {
			this._updateText.setText("Never");
			this._codeDataText.setText(e.getMessage());
			return;
		}
		this._updateText.setText(codeData[1].toString());
		this._codeDataText.setText(codeData[0].toString());
	}
}
