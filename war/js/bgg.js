function show_children(element, selector, link_selector) {
	$(selector).show();
	$(link_selector).text('-')
	$(element).unbind("click").click(function() { 
		hide_children(element, selector, link_selector)
	});
}

function hide_children(element, selector, link_selector) {
	$(selector).hide();
	$(link_selector).text('+')
	$(element).unbind("click").click(function() { 
		show_children(element, selector, link_selector)
	});	
}
