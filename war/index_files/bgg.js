function show_children(element, selector) {
	$(selector).show();
	$(element).unbind("click").click(function() { 
		hide_children(element, selector)
	});
}

function hide_children(element, selector) {
	$(selector).hide();
	$(element).unbind("click").click(function() { 
		show_children(element, selector)
	});	
}
