function loadWidgets(state){
	for (var i in state['widgets']){
		appendWidget(state.widgets[i]);
	}
}

function createColumns(state){
	for (var i in state['columnSizes']){
		appendColumn(state.columnSizes[i],i);
	}
}

widgetColumnWrap = 'widgetColumnWrap';

function getWidgetCode(widget){
	return '<div class="widget">'+
			getTitleDiv(widget['title'])+
			'<div class="widgetBody">'+
				
				'<input class="widgetConfig" type="hidden" name="title" value="'+widget['title']+'"/>'+
				'<input class="widgetConfig" type="hidden" name="url" value="'+widget['url']+'"/>'+
				'<input class="widgetConfig" type="hidden" name="height" value="'+widget['height']+'"/>'+
				'<input class="widgetConfig" type="hidden" name="column" value="'+widget['column']+'"/>'+
				
				'<iframe src="'+widget['url']+'" height="'+widget['height']+'""></iframe><br>'+
			'</div>'+
		'</div>';
}

function replaceWidget(widgetDescriptor, widgetToReplace){
	widgetToReplace.replaceWith(getWidgetCode(widgetDescriptor));
}

function appendWidget(widget){
	var columnIndex = widget['column'];
	$('#'+addWidgetLink+widget['column']).replaceWith(getWidgetCode(widget));
	addWidgetAddLink(columnIndex);
}

function closeWidget(closeButton){
	$(closeButton).parent().parent().remove();
}

function configWidget(configButton){
	var widget = {};
	var widgetElement = $(configButton).parent().parent();
	widgetElement.children(".widgetBody:first").children(".widgetConfig").each(function() {
			console.log($(this).attr("name")+':'+$(this).val());
			widget[$(this).attr("name")]=$(this).val();
	});
	var title = widget['title'];
	var url = widget['url'];
	var height = widget['height'];
	var column = widget['column'];
	var configWidget = getConfigWidget(title,url,height,column);
	widgetElement.replaceWith(configWidget);
	
}

function getTitleDiv(title){
	return '<div class="widgetTitle">'+title+
			'<img class="closeButton" onclick="closeWidget(this)" src="./images/closeButton.png"/>'+
			'<img class="closeButton" onclick="configWidget(this)" src="./images/configButton.png"/>'+
		'</div>';
}

addWidgetLink = 'addWidgetLink';

function addNewWidgetForButton(button){
	var widget = {};
	var widgetBody = $(button).parent()
	widgetBody.children("input").each(function() {
			widget[$(this).attr("name")]=$(this).val();
	});
	replaceWidget(widget, widgetBody.parent());
}

function getConfigWidget(title,url,height,column){
	return '<div class="widget">'+
		getTitleDiv(title)+
			'<div class="widgetBody">'+
				'Title:<br>'+
				'<input type="text" name="title" value="'+title+'" style="width:100%;"/><br>'+
				'Url:<br>'+
				'<input type="text" name="url" value="'+url+'" style="width:100%;"/><br>'+
				'Height:<br>'+
				'<input type="text" name="height" value="'+height+'" style="width:100%;"/><br>'+
				'<input type="hidden" name="column" value="'+column+'"/><br>'+
				'<input type="button" value="Add" onclick="addNewWidgetForButton(this)"/><br>'+
			'</div>'+
		'</div>';
}

function appendBlankWidget(columnIndex){
	var title = "Dontpad/sneer_dashboard";
	var url = "http://dontpad.com/sneer_dashboard";
	var height = "200px";
	$('#'+addWidgetLink+columnIndex).replaceWith(getConfigWidget(title,url,height,columnIndex));
	addWidgetAddLink(columnIndex);
}

function addWidgetAddLink(columnIndex){
	$('#'+widgetColumnWrap+columnIndex).append('<div id="'+addWidgetLink+columnIndex+'" class="'+addWidgetLink+'" style="width:100%;"> <a href="javascript:appendBlankWidget('+columnIndex+');" >+</a> </div>');
}

function appendColumn(size,columnIndex){
	$('#widgetTable tr:first').append('<td class="widgetColumn" style="width:'+size+';"> <div id="'+widgetColumnWrap+columnIndex+'" class="col"></div> </td>');
	addWidgetAddLink(columnIndex);
}
