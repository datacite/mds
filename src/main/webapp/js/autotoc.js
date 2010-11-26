function autoTOC(rootId, tocId, depth, cssClassPrefix, anchorPrefix) {
	var toc = dojo.create("ul", null, tocId);
	var anchor = 0;

	dojo.query("h1,h2,h3,h4,h5,h6", rootId).forEach(function(h) {
		var level = h.nodeName.charAt(1);
		if (level > depth)
			return;
		anchor++;
		var anchorName = anchorPrefix + "-" + anchor;
		var cssClass = cssClassPrefix + "-h" + level;
		var li = dojo.create("li", {
			class : cssClass
		}, toc);
		var link = dojo.create("a", {
			href : "#" + anchorName,
			innerHTML : h.innerHTML
		}, li);
		dojo.create("a", {
			name : anchorName
		}, h);
	});
}