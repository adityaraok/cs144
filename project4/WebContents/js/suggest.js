function SuggestCtrl(searchBar, searchContainer) {
    this.cur = -1;
    this.layer = null;
    this.searchBar = searchBar;
    this.searchContainer = searchContainer;
    this.init();
}


SuggestCtrl.prototype.init = function () {
    oThis = this;

    this.searchBar.onkeyup = function(e) {
        switch(e.keyCode) {
            case 38: //up arrow
                oThis.previousSuggestion();
                break;
            case 40: //down arrow 
                oThis.nextSuggestion();
                break;
            case 13: //enter
                oThis.hideSuggestions();
                break;
            default:
                oThis.fetchSuggestions();
        }
        
    }

  
    this.searchBar.onblur = function () {
        oThis.hideSuggestions();
    }

    this.buildSuggestBox();
}


SuggestCtrl.prototype.fetchSuggestions = function () { 
   
    var text = escape(this.searchBar.value);

    if (text == "") {
        this.hideSuggestions();
        return;
    }


    var request = new XMLHttpRequest();
    var oThis = this;

    request.onreadystatechange = function() {
        if (request.readyState == 4 && request.status == 200) {
           
            var suggestions = oThis.parseSuggestions(request.responseXML);

            if(suggestions.length > 0)
                oThis.showSuggestions(suggestions);
            else
                oThis.hideSuggestions();
        }
    }
    var url = "/eBay/suggest?q=" + text;
    request.open("GET", url, true);
    request.send();
};

SuggestCtrl.prototype.parseSuggestions = function (response) {
    var suggestions = Array();

    if (typeof response === "undefined") 
        return [];

    var completeSuggestions = response.documentElement.childNodes;

    for(var i = 0; i < completeSuggestions.length; i++) {
        suggestions[i] = completeSuggestions[i].childNodes[0].getAttribute("data"); 
    }


    return suggestions;

};


SuggestCtrl.prototype.showSuggestions = function (suggestions) {

    var div = null;
    this.layer.innerHTML = "";

    for (var i=0; i < suggestions.length; i++) {
        div = document.createElement("div");
        div.appendChild(document.createTextNode(suggestions[i]));
        this.layer.appendChild(div);
    }

    this.layer.style.left = this.getLeft() + "px";
    this.layer.style.top = (this.getTop() + this.searchBar.offsetHeight) + "px";
    this.layer.style.visibility = "visible";
};

SuggestCtrl.prototype.buildSuggestBox = function (suggestions) {
    this.layer = document.createElement("div");
    this.layer.className = "suggestions";
    this.layer.style.visibility = "hidden";
    this.layer.style.width = this.searchBar.offsetWidth + "px";
    this.searchContainer.appendChild(this.layer);

    var oThis = this;

    this.layer.onmousedown = this.layer.onmouseup = 
    this.layer.onmouseover = function (e) {
        e = e || window.event;
        target = e.target || e.srcElement;

        if (e.type == "mousedown") {
            oThis.searchBar.value = target.firstChild.nodeValue;
            oThis.hideSuggestions();
        } else if (e.type == "mouseover") {
            oThis.highlightSuggestion(target);
        } else {
            oThis.searchBar.focus();
        }

    };
};

SuggestCtrl.prototype.highlightSuggestion = function (suggestionNode) {

    for (var i=0; i < this.layer.childNodes.length; i++) {
        var current = this.layer.childNodes[i];
        if(current == suggestionNode)
            current.className = "current";
        else if (current.className == "current")
            current.className = "";
    
    }
};

SuggestCtrl.prototype.nextSuggestion = function () {
    var nodes = this.layer.childNodes;

    if (nodes.length > 0 && this.cur < nodes.length) {
        var node = nodes[++this.cur];
        this.highlightSuggestion(node);
        this.searchBar.value = node.firstChild.nodeValue;
    }
}

SuggestCtrl.prototype.previousSuggestion = function () {
    var nodes = this.layer.childNodes;

    if (nodes.length > 0 && this.cur > 0) {
        var node = nodes[--this.cur];
        this.highlightSuggestion(node);
        this.searchBar.value = node.firstChild.nodeValue;
    }
}

SuggestCtrl.prototype.hideSuggestions = function () {
    this.layer.style.visibility = "hidden";
};



SuggestCtrl.prototype.getLeft = function () {

    var node = this.searchBar;
    var left = 0;

    while(node.tagName != "BODY") {
        left += node.offsetLeft;
        node = node.offsetParent;
    }

    return left;
}


SuggestCtrl.prototype.getTop = function () {

    var node = this.searchBar;
    var top = 0;

    while(node.tagName != "BODY") {
        top += node.offsetTop;
        node = node.offsetParent;
    }

    return top;
}


