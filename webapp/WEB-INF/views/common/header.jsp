<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="taglibs.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
<title>Gruter-LogGenerator</title>
<!--link rel="shortcut icon" type="image/x-icon" href="<c:url value='/resources/images/icons/favicon.ico'/>" /-->
<!-- Ext -->
<link rel="stylesheet" type="text/css" href="<c:url value='/resources/extjs/resources/css/ext-all.css'/>">
<style type="text/css">
p {
	margin: 5px;
}
.nav {
	background-image: url(/resources/images/icons/fam/folder_go.png);
}
.info {
	background-image: url(/resources/images/icons/fam/information.png);
}

.stop {
	background-image: url(/resources/images/icons/fam/delete3.gif);
}

.pause {
	background-image: url(/resources/images/icons/fam/delete.gif);
}

.resume {
	background-image: url(/resources/images/icons/fam/rollback.gif);
}

.msg .x-box-mc {
    font-size:14px;
}
#msg-div {
    position:absolute;
    left:65%;
    top:10px;
    width:300px;
    z-index:20000;
}
#msg-div .msg {
    border-radius: 8px;
    -moz-border-radius: 8px;
    background: #F6F6F6;
    border: 2px solid #ccc;
    margin-top: 2px;
    padding: 10px 15px;
    color: #555;
}
#msg-div .msg h3 {
    margin: 0 0 8px;
    font-weight: bold;
    font-size: 15px;
}
#msg-div .msg p {
    margin: 0;
}
.settings {
    background-image: url(/resources/images/icons/fam/gears.gif) !important;
}
.add {
    background-image: url(/resources/images/icons/fam/add.gif) !important;
}
.option {
    background-image: url(/resources/images/icons/fam/plugin.gif) !important;
}
.remove {
    background-image: url(/resources/images/icons/fam/delete.gif) !important;
}
.delete {
    background-image: url(/resources/images/icons/fam/delete2.gif) !important;
}
.save {
    background-image: url(/resources/images/icons/fam/save.gif) !important;
}
.reset {
    background-image: url(/resources/images/icons/fam/stop.png) !important;
}
.rellback {
    background-image: url(/resources/images/icons/fam/rollback.gif) !important;
}
.serverconfig {
    background-image: url(/resources/images/icons/fam/configs.png) !important;
}
.treeicon {
    background-image: url(/resources/images/icons/fam/list-items.gif) !important;
}
.dashboardicon {
    background-image: url(/resources/images/icons/fam/album.gif) !important;
}

.logout {
    background-image: url(/resources/images/icons/logout.gif) !important;
}

.barchart {
    background-image: url(/resources/images/icons/fam/chart48x48.png) !important;
}

.x-grid-checkheader {
    height: 14px;
    background-image: url('/resources/extjs/resources/themes/images/default/grid/unchecked.gif');
    background-position: 50% -2px;
    background-repeat: no-repeat;
    background-color: transparent;
}

.x-grid-checkheader-checked {
    background-image: url('/resources/extjs/resources/themes/images/default/grid/checked.gif');
}

.x-grid-checkheader-editor .x-form-cb-wrap {
    text-align: center;
}

/* style rows on mouseover */
.x-grid-row-over .x-grid-cell-inner {
    font-weight: bold;
}
/* shared styles for the ActionColumn icons */
.x-action-col-cell img {
    height: 16px;
    width: 16px;
    margin-left:4px;
    cursor: pointer;
}
/* custom icon for the  ActionColumn icon */
.x-action-col-cell img.delete {
    background-image: url(/resources/images/icons/fam/delete.gif);
}
.x-action-col-cell img.rollback {
    background-image: url(/resources/images/icons/fam/rollback.gif);
}

.red-node {
	color: red;
}

.red-icon {
	background-color: red;
}

</style>
<script type="text/javascript" src="<c:url value='/resources/extjs/ext-all.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/extjs/ux/statusbar/StatusBar.js'/>"></script>
<script type="text/javascript">
/*------------ Extjs bug fix----------------- */
//add a workaround for bug where boundItems aren't properly
//recalculated when necessary
//if there are 0 bound items, we will always recheck
Ext.form.Basic.override({
	getBoundItems : function() {
		var boundItems = this._boundItems;
		if (!boundItems || boundItems.getCount() == 0) {
			boundItems = this._boundItems = Ext.create('Ext.util.MixedCollection');
			boundItems.addAll(this.owner.query('[formBind]'));
		}
	return boundItems;
	}
});

var clock = Ext.create('Ext.toolbar.TextItem', {text: Ext.Date.format(new Date(), 'g:i:s A')});

Ext.Ajax.on('requestcomplete', function(conn, response, options){
	//console.log('arguments: %o', arguments);
	});

Ext.Ajax.on('requestexception', function(conn, response, options){
	//console.log('exception arguments: %o', arguments);
		if(response.status == 403){
			delete options.failure;
			delete options.callback;
			try{
				var responseJson = Ext.decode(response.responseText);
	      		if (responseJson.success === false && responseJson.url) { 
	      			window.location = responseJson.url;
	      		}
			}catch(e){
				//console.error(e);
				window.location = '/';
			}
		}else if(response.status == 404){
			delete options.failure;
			delete options.callback;
			alert(response.responseText);
		}else if(response.status == 200){
			try{
				var responseJson = Ext.decode(response.responseText);
	      		if (responseJson.success === false && responseJson.status === 302) {
	      			delete options.failure;
	    			delete options.callback;
	      			alert(responseJson.msg);
	      		}
			}catch(e){
			}
		}
	});
/*-------------------------------------------------  */

/* -----------------Validator------------------------------- */
  Ext.apply(Ext.form.field.VTypes, {
	    IPAddress:  function(v) {
	        return /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/.test(v);
	    },
	    IPAddressText: 'Must be a numeric IP address',
	    IPAddressMask: /[\d\.]/i
	});
  Ext.apply(Ext.form.field.VTypes, {
	    Port:  function(v) {
	        return /^[\d]+$/i.test(v);
	    },
	    PortText: 'Must be a numeric Port',
	    PortMask: /[\d]+/i
	});
 
 /* -------------------------------------------------------  */
Ext.define('LogGenerator', {
	singleton: true,
	onSuccessOrFailForm : function(form, action) {
		// form callback
	    var result = action.result;
	    if (result.success) {
	      	//Ext.MessageBox.alert('Success',action.result.msg);
	    }
	    else {
	    	switch (action.failureType) {
	            case Ext.form.action.Action.CLIENT_INVALID:
	                Ext.Msg.alert('Failure', 'Form fields may not be submitted with invalid values');
	                break;
	            case Ext.form.action.Action.CONNECT_FAILURE:
	                Ext.Msg.alert('Failure', 'Ajax communication failed');
	                break;
	            case Ext.form.action.Action.SERVER_INVALID:
	               Ext.Msg.alert('Failure', action.result.msg);
	       }
	    }
	  },
	onFailureAjax: function(response) {
		//ajax request failure callback
     		var responseJson = Ext.decode(response.responseText);
     		Ext.Msg.alert('failure', responseJson.msg);
  	},
  	exceptionListener: function(proxy, response, op, args) {
  		//proxy exception listener
    	if(op.action == 'read'){
			Ext.Msg.alert('Error', proxy.reader.jsonData.msg);
        }
    },
    storeCallback: function(records, operation, success) {
        //the operation object contains all of the details of the load operation
        if(!success)
        	Ext.Msg.alert('Error', proxy.reader.jsonData.msg);
    	 	//console.log('load failed -- arguments: %o', arguments);
    },
    msgCt : null,

    createBox : function(t, s){
       return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
    },
    msg : function(title, format){
    		if(!title) title = '';
    		if(!format) format = '';
    		
            if(!this.msgCt){
               this.msgCt = Ext.core.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
            }
            var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
            var m = Ext.core.DomHelper.append(this.msgCt, this.createBox(title, s), true);
            m.hide();
            m.slideIn('t').ghost("t", { delay: 1000, remove: true});
     }

});
	
getTopMenuItems = function(){
	return  [' ', {
				scale:'medium',
				text: 'Generator',
				url : '/main.do',
				target: '_self'
			}
		];
};
    
</script>