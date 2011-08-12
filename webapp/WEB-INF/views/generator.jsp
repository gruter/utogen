<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="./common/meta.jsp"%>
<%@ include file="./common/header.jsp"%>

<script type="text/javascript">

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', '<c:url value="/resources/extjs/ux"/>');
Ext.require([
             'Ext.data.*',
             'Ext.panel.Panel',
             'Ext.view.View',
             'Ext.layout.container.Fit',
             'Ext.toolbar.Paging',
             'Ext.ux.form.SearchField'
         ]);
         
Ext.onReady(function() {
	createGenerateFormCmp = function(){ 
		return {
			title: "Generator prorpeties",
			xtype: 'form',
	        frame: true,
	        bodyPadding: 5,
	        border :false,
	        waitMsgTarget: true,
	        id: 'generate-form', 
	        defaultType: 'textfield',
	        fieldDefaults: {
	            labelAlign: 'left',
	            labelWidth: 100,
	            anchor: '100%'
	        },
	        items: [ 
	    	        {
	                	xtype: 'textfield',
			            name: 'name',
			            fieldLabel: 'Name',
			            allowBlank: false
	    	        }, {
	                	xtype: 'textfield',
			            name: 'description',
			            fieldLabel: 'Description',
			            allowBlank: true    
			        }, {
	                	xtype: 'textfield',
			            name: 'delimiter',
			            fieldLabel: 'Delimiter',
			            allowBlank: false    
			        }, {
	                	xtype: 'textfield',
			            name: 'totalRecords',
			            fieldLabel: 'Total Records',
			            allowBlank: false
			        },{
	                	xtype: 'textfield',
			            name: 'tps',
			            fieldLabel: 'Records per sec.'
			        },{
	                	xtype: 'textfield',
			            name: 'log4jName',
			            fieldLabel: 'Logger name',
			            allowBlank: false
			        },{
			            xtype: 'textareafield',
			            name: 'dataItemXml',
			            rows: 10,
			            fieldLabel: 'Filed Xml',
			            labelAlign: 'top',
			            margins: '0',
			            allowBlank: false
			        },{
			            xtype: 'textareafield',
			            name: 'log4j',
			            rows: 10,
			            fieldLabel: 'Log4J',
			            labelAlign: 'top',
			            flex: 1,
			            margins: '0',
			            allowBlank: false
			        }			          
			],
	        buttons: [ {
	            text: 'Generate',
	            disabled: true,
	            formBind: true, //only enabled once the form is valid
	            handler: function(){
	            	var fileDefinition = this.up('form').getForm().getFieldValues();
	            	var confirm =Ext.MessageBox.confirm(
	              			'Are you sure?', 
	            			'Please confirm the generating records of ' + fileDefinition['name'],
	            			function(btn) {
              					if (btn != 'yes') return;
              					
	              				Ext.getCmp('generate-form').getForm().submit({
				            	    clientValidation: true,
				            	    url: 'generate.do',
				            	    /*params: {
				            	        params: Ext.encode(fileDefinition),
				            	    },*/
				            	    success: function(form, action) {
				            	    	Ext.getCmp('generator-job-listview').store.load();
				            	    	alert('Job Started. JobId=' + action.result.data);
				    	      			//LogGenerator.msg(action.result.msg, action.result.data.value);
				            	    },
				            	    failure: function(form, action) {
				            	    	LogGenerator.onSuccessOrFailForm(form, action);
				            	    }
				            	});
	       			});
	            }
	        }]
	    };
	};
	createGeneratorJobCmp = function() {
		return Ext.create('Ext.ListView', {
			hideHeaders : false,	
			layout: 'fit',
			id : 'generator-job-listview',
			store : Ext.create('Ext.data.JsonStore', {
				fields : [ {name: 'jobId', type: 'string'},
						   {name: 'definitionName',type: 'string'},
						   {name: 'generatedRecords', type: 'int'},
						   {name: 'jobStatus', type: 'string'}
				],
				proxy : Ext.data.ScriptTagProxy({ 
					type : 'ajax',
					url : 'get_jobs.do'
				}),
				listeners: {
					'load': function(view, records, success, op) {
					}
				}			
			}),
			columns : [ {
					header: 'Id',
					menuDisabled : true,
					dataIndex: 'jobId',
					width: 50
				}, {
					header: 'Name',
					menuDisabled : true,
					dataIndex: 'definitionName',
					flex: 1
				}, {
					header : '# Record',
					menuDisabled : true,
					dataIndex : 'generatedRecords',
					align: 'right',
					width: 100,
					renderer: Ext.util.Format.numberRenderer('0,0')
				}, {
					header : 'Status',
					menuDisabled : true,
					dataIndex : 'jobStatus',
					width: 80
				}, {
					xtype: 'actioncolumn',
					header: 'stop',
	                width: 30,
	                align: 'center',
	                items: [{
	                	iconCls : 'stop',  
	                    tooltip: 'Stop',
	                    handler: function(grid, rowIndex, colIndex) {
	                        var rec = Ext.getCmp('generator-job-listview').store.getAt(rowIndex);
	                		if (rec.get('jobStatus') == 'End') {
		                		alert('Ended job');
		                		return;
	                		}
	                        Ext.Ajax.request({
	        			        url: 'handle_job.do?mode=stop&jobId=' + rec.get('jobId'),
	        			        success: function(response, opts) {
	        			        	alert(rec.get('jobId') + " stoped");
	        			        	Ext.getCmp('generator-job-listview').store.load();
	        			        },
	        			        failure: function(response, opts) {
	        			        	LogGenerator.onFailureAjax(response);
	        			        } 
	        			    });	
	                    }
	                }]
				}, {
					xtype: 'actioncolumn',
					header: 'pause',
	                width: 30,
	                align: 'center',
					items: [ {
	                    getClass: function(v, meta, rec) {          // Or return a class from a function
	                        if (rec.get('jobStatus') == 'Running') {
	                            this.items[0].tooltip = 'Pause';
	                            return 'pause';
	                        } else if (rec.get('jobStatus') == 'Pause') {
	                            this.items[0].tooltip = 'Resume';
	                            return 'resume';
	                        } else {
	                        	this.items[0].tooltip = 'Pause';
	                            return 'pause';
	                        }
	                    },
	                    handler: function(grid, rowIndex, colIndex) {
	                        var rec = Ext.getCmp('generator-job-listview').store.getAt(rowIndex);
	                		if (rec.get('jobStatus') == 'End') {
		                		alert('Ended job');
		                		return;
	                		}
	                        var mode = "";
	                        if(rec.get('jobStatus') == 'Running') {
								mode = "pause";
		                    } else if (rec.get('jobStatus') == 'Pause') {
			                    mode = "resume";
	                        }
	                        if(mode == "") {
		                        return;
	                        }
	                        Ext.Ajax.request({
	        			        url: 'handle_job.do?mode=' + mode + '&jobId=' + rec.get('jobId'),
	        			        success: function(response, opts) {
									alert(rec.get('jobId') + " " + mode);
	        			        	Ext.getCmp('generator-job-listview').store.load();
	        			        },
	        			        failure: function(response, opts) {
	        			        	LogGenerator.onFailureAjax(response);
	        			        } 
	        			    });	                        
	                    }
	                }]
				}
			],
			dockedItems: [{
	    	    xtype: 'toolbar',
	    	    dock: 'top',
	    	    items: [
		    	    '->', {
		            	text   : 'Reload',
		            	iconCls : 'x-tbar-loading',
		                handler: function() {
							Ext.getCmp('generator-job-listview').store.load();
		    	    	}
	            	}
		        ]
	    	}]			
	    });		
	};
	
	createFileDefinitionCmp = function() {
		return Ext.create('Ext.ListView', {
			hideHeaders : false,	
			layout: 'fit',
			id : 'file-definition-listview',
			store : Ext.create('Ext.data.JsonStore', {
				fields : [ {name: 'name', type: 'string'},
						   {name: 'log4jName',type: 'string'},
						   {name: 'description', type: 'string'},
						   {name: 'totalRecords', type: 'string'},
						   {name: 'tps', type: 'string'},
						   {name: 'delimiter', type: 'string'},
						   {name: 'dataItemXml', type: 'string'},
						   {name: 'log4j', type: 'string'}
				],
				proxy : Ext.data.ScriptTagProxy({ 
					type : 'ajax',
					url : 'get_file_definitions.do'
				}),
				listeners: {
					'load': function(view, records, success, op) {
					}
				}			
			}),
			columns : [ {
					header: 'Name',
					menuDisabled : true,
					dataIndex: 'name',
					width: 100
				}, {
					header: 'Logger',
					menuDisabled : true,
					dataIndex: 'log4jName',
					width: 100
				}, {
					header : 'Description',
					menuDisabled : true,
					dataIndex : 'description',
					flex: 1
				}
			],
			listeners : {
				'itemclick' : function(grid, record, e) {
					if (record) {
						var selectedRecord = Ext.getCmp('file-definition-listview').getSelectionModel().getLastSelected();
						var genForm = Ext.getCmp('generate-form').getForm();
						genForm.findField("name").setValue(record.get('name'));
						genForm.findField("description").setValue(record.get('description'));
						genForm.findField("totalRecords").setValue(record.get('totalRecords'));
						genForm.findField("tps").setValue(record.get('tps'));
						genForm.findField("log4jName").setValue(record.get('log4jName'));
						genForm.findField("dataItemXml").setValue(record.get('dataItemXml'));
						genForm.findField("log4j").setValue(record.get('log4j'));
						genForm.findField("delimiter").setValue(record.get('delimiter'));
			        }
				}
			},
			dockedItems: [{
	    	    xtype: 'toolbar',
	    	    dock: 'top',
	    	    items: [
		    	    '->', {
		            	text   : 'Reload',
		            	iconCls : 'x-tbar-loading',
		                handler: function() {
							var grid = Ext.getCmp('file-definition-listview');
							grid.store.load();
		    	    	}
	            	}
		        ]
	    	}]			
	    });
	};
	
    var viewport = Ext.create('Ext.Viewport', {
        layout: 'border',
        items: [{
        	cls:'x-panel-header-default',
        	border : false,
        	frame:true,
        	region: 'north',        	
        	margins: '0 5 10 5',
        	height: 45,
            dockedItems: [{
        	    xtype: 'toolbar',
        	    dock: 'top',
        	    style: {background: 'none'},
        	    items: [getTopMenuItems()]
        	}]
		}, {
			region : 'west',
			split : true,
			margins : '0 0 0 5',
			title : 'File Definition',
			layout: 'fit',
			width : 250,
			minWidth : 175,
			//maxWidth : 400,
			collapsible : true,
			animCollapse : true,
			iconCls : 'nav',
			bodyBorder : true,
			items: [
					createFileDefinitionCmp()
			]        	
        },{
        	region: 'center',
            split: true,
            items: [
               createGenerateFormCmp()
            ]
        }, {
        	region : 'east',
			split : true,
			margins : '0 5 0 0',
			title : 'Generator Job',
			layout: 'fit',
			width : 400,
			minWidth : 175,
			collapsible : true,
			animCollapse : true,
			iconCls : 'nav',
			bodyBorder : true,			
			items: [createGeneratorJobCmp()]        	
        }]
    });
    Ext.getCmp('file-definition-listview').store.load();
    Ext.getCmp('generator-job-listview').store.load();
});

</script>
</head>
<body>
</body>
</html>
<%
/*
fdsafsd

File Definitions   Input    JobList
 reload                      reload, pause, resume, stop
*/
%> 