var MODES = { LETTER: 'LETTER', CATEGORY: 'CATEGORY', NOVEL: 'NOVEL', EPOCH: 'EPOCH' };

var app = Marionette.Application.extend({
    initialize: function(options) {
        this.mode = MODES.LETTER;
        this.SHOWING = { NOVEL : false, EPOCH: true, CATEGORY: true }; 
        this.currentLetter;
        this.currentCategoryID;
        this.currentNovelID;
        this.currentEpochID;
    },
    isShowing: function(key) {
        return this.SHOWING[key];
    },
    setShowing: function(key,bool) {
        this.SHOWING[key] = bool;
        return this;
    },
    toggleShowing: function(key) {
        this.SHOWING[key] = !this.SHOWING[key];
        return this.isShowing(key);
    },
    isInContext: function(thisTerm) { 
        var self = this;
        if (thisTerm.get('id') === null || thisTerm.get('id') === 0) {
            return true;
        }
        switch(this.mode) {
            case 'LETTER':
                return thisTerm.get('name').substring(0,1).toLowerCase() === self.currentLetter.toLowerCase() || 
                     (thisTerm.get('name').substring(0,1) === '&' && thisTerm.get('name').substring(1,2) === self.currentLetter);
            case 'CATEGORY':
                var match = false;
                _.each(thisTerm.get('allCategories'), function(cat) {
                    if (!!cat.id && cat.id !== 0 && cat.id === self.currentCategoryID) match = true;
                });
                return match;
            case 'NOVEL':
                var match = false;
                _.each(thisTerm.get('novels'), function(novel) {
                    if (!!novel.id && novel.id !== 0 && novel.id === self.currentNovelID) match = true;
                });
                return match;
            case 'EPOCH':
                var e = thisTerm.get('eventDate');
                if (!e || !e.epoch) {
                    return false;
                }
                return e.epoch.id = self.currentEpochID;
            default:
                return false;
        }
    },
    getMode: function() {
        return this.mode;
    },
    storeMode: function(mode,thing) {
        this.setMode(mode);
        var self = this;
        switch(mode) {
            case 'LETTER':
                self.currentLetter = thing;
                break;
            case 'CATEGORY':
                self.currentCategoryID = thing;
                break;
            case 'NOVEL':
                self.currentNovelID = thing;
                break;
            case 'EPOCH':
                self.currentEpochID = thing;
                break;
        }
        return this;
    },
    getModeIdentifier: function() {
        var self = this;
        switch(this.mode) {
            case 'LETTER':
                return self.currentLetter;
            case 'CATEGORY':
                return self.currentCategoryID;
            case 'NOVEL':
                return self.currentNovelID;
            case 'EPOCH':
                return self.currentEpochID;
        }
    },
    setMode: function(mode) {
        this.mode = mode;
        return this;
    },
    initializeCategorySelector: function($selDiv,categories) { 
        var self = this;
        var $container = $selDiv.closest('td.category-container');
        var $sel = $container.find('select');
        var $outputDiv = $container.find('.category-selector');
        var byId = { };
        _.each(categories, function(category) {
            self.recurseCategoryIds(category,byId);
        });
        self.writeCategoriesToTermInput($sel,$outputDiv,categories,byId);
    },
    recurseCategoryIds: function(category,byId) {
        var self = this;
        byId[category.id] = category;
         _.each(category.children, function(icategory) {
            self.recurseCategoryIds(icategory,byId);
        });
    }, 
    recurseCategorySelections: function(category,selected,matches,byId) {
        var self = this;
        if (selected[category.id]) {
            matches.push(category);
        }
         _.each(category.children, function(icategory) {
            self.recurseCategorySelections(icategory,selected,matches,byId);
        });
    },
    writeCategoriesToTermInput: function($selector,$outputDiv,categories,byId) { 
        var self = this;
        $outputDiv.empty();
        var matches = [];
        var selected = {};
        $selector.find('option:selected').each(function() {  selected[$(this).val()] = true; });
        _.each(categories, function(category) {
            self.recurseCategorySelections(category,selected,matches,byId);
        });
        for (var i = 0, j = matches.length; i < j; i++) {
            var category = matches[i];
            var hierarchy = self.buildCategoryHierarchy(category,byId);
            var $div = $('<div />').css({'padding-bottom':'6px'});
            $div.append(
                $('<span />').addClass('icon icon-orange icon-delete').attr('title','Remove Category').css({'margin-right':'4px'})
                    .on('click', function() { self.removeTermCategory($selector,$outputDiv,categories,byId,category); })
            );
            var length = hierarchy.length;
            _.each(hierarchy, function(category,ind) {
                var $idiv = $('<span />').addClass('category-part').html(category.name);
                if ((ind+1) === length) {
                    $idiv.addClass('category-part-last');
                }
                $div.append($idiv);
            });
            $outputDiv.append($div);
        }
        if (!matches.length) {
            $outputDiv.append($('<div />').append('No Current Selection'));
        }
        var $btn = $('<span />').addClass('icon icon-blue icon-create').attr('title','Add Another Category').css({'margin-right':'4px','font-size':'.9em'})
            .on('click',function() { self.promptAddTermCategory($selector,$outputDiv,categories,byId); });
        var $a = $('<a href="javascript:void(false)" />').attr('title','Add Another Category').html('Add Another Category')
            .on('click',function() { self.promptAddTermCategory($selector,$outputDiv,categories,byId); });
        $outputDiv.append($('<div />').css({'padding-left':'25px','padding-bottom':'2px'}).append($btn).append($a));
        $outputDiv.pxTooltip();
    },
    onTermCategorySubSelection: function($selector,$outputDiv,categories,byId,ref) {
        var self = this;
        // REMOVE EVERYTHING AFTER THIS
        var $span = $(ref).next('span.customSelect');
        $span.nextAll('*').remove();
        // NOW ADD IN APPROPRIATE SHIT
        var id = parseInt($(ref).val());
        if (id !== 0 && byId[id] && byId[id].children.length) {
            var $select = $('<select />').addClass('new-category-part')
                .on('change',function() { self.onTermCategorySubSelection($selector,$outputDiv,categories,byId,this); });
            var category = byId[id];
            $select.append($('<option />').val(0).text('--Select Below--')); 
                _.each(category.children, function(child) {
                    $select.append($('<option />').val(child.id).text(child.name)); 
            });
            $(ref).closest('div.new-category-holder').append($select);
            $select.customSelect();
        }
    },
    promptAddTermCategory: function ($selector,$outputDiv,categories,byId) {
        var self = this;
        var $content = $('<div />').addClass('new-category-holder').css({'width':'1200px'});
        var $select = $('<select />').addClass('new-category-part')
            .on('change',function() { self.onTermCategorySubSelection($selector,$outputDiv,categories,byId,this); });
        $select.append($('<option />').val(0).text('--Select Below--')); 
        _.each(categories, function(category) {
           $select.append($('<option />').val(category.id).text(category.name)); 
        });
        $content.append($select);
        var submitFunction = function(f,$dialog) { 
            var id = parseInt($dialog.find('select').last().val());
            var cnt = $dialog.find('select').length;
            if (id === 0 && cnt > 1) {
                // CHECK PREVIOUS TO LAST
                // THIS IS 1-INDEXED
                id = parseInt($dialog.find('select:nth-child(' + (cnt-1) + ')').val());
            }
            $selector.append($('<OPTION />').val(id).text('NA').prop('selected',true));
            self.writeCategoriesToTermInput($selector,$outputDiv,categories,byId);
            return true;
        };
        var verifyFunction = function(f,$dialog) {
            var id = parseInt($dialog.find('select').last().val());
            var cnt = $dialog.find('select').length;
            if (id === 0 && cnt > 1) {
                // CHECK PREVIOUS TO LAST
                // THIS IS 1-INDEXED
                id = parseInt($dialog.find('select:nth-child(' + (cnt-1) + ')').val());
            }
            if (id === 0) {
                textDialog('You must select a new, valid Category!','No Category Selected');
                return false;
            }
            if (isNaN(id)) {
                textDialog('Please check that it is not already present!','Error Adding Category');
                return false;
            }
            if ($selector.find("option[value='" + id + "']").length) {
                textDialog('Please check that it is not already present!','Error Adding Category');
                return false;
            }
            return true;
        };
        var $dialog = promptDialog($content,'Choose Category',verifyFunction,submitFunction,{});
        $select.customSelect();
    },
    removeTermCategory: function($selector,$outputDiv,categories,byId,categoryToRemove) {
        $selector.find("option[value='" + categoryToRemove.id + "']").remove();
        this.writeCategoriesToTermInput($selector,$outputDiv,categories,byId);
    },
    buildCategoryHierarchy: function(category,byId) {
        var hierarchy = [];
        hierarchy.unshift(category);
        var current = category;
        while (current.parentID) {
            current = byId[current.parentID];
            if (!!current) {
                hierarchy.unshift(current);
            }
        }
        return hierarchy;
    }
});

var glossary = new app({container: '#glossary'});

glossary.addRegions({
    top: '#topNavElement',
    innerTop: '#innerTopNavElement',
    left: '#leftNavElement>div',
    main: '#mainElement>div'
});

var Letter = Backbone.Model.extend({
    initialize: function (options) {
        this.set({selected: options.selected === null ? false : options.selected});
        this.set({ value: options.value });
    },       
    setSelected: function(bool) {
        this.set({ selected: bool });
    },
    isSelected: function() {
        return this.get('selected');
    }
});

var Letters = Backbone.Collection.extend({ model: Letter });

var LETTERS = new Letters([{ value:'A', selected:true }, {value:'B'}, {value:'C'}, {value:'D'}, {value:'E'}, {value:'F'}, {value:'G'}, {value:'H'}, {
        value:'I'}, {value:'J'}, {value:'K'}, {value:'L'}, {value:'M'}, {value:'N'}, {value:'O'}, {value:'P'}, {value:'Q'}, {value:'R'}, {value:'S'}, {
        value:'T'}, {value:'U'}, {value:'V'}, {value:'W'}, {value:'X'}, {value:'Y'}, {value:'Z' }]);

var LetterView = Backbone.Marionette.ItemView.extend({
    template: "#letter-template",
    tagName: 'li',
    className: 'letter',
    events: {
        'click a': 'select'
    },
    select: function() {
        glossary.trigger('select:letter', this.model);
    }
});

var LettersView = Backbone.Marionette.CompositeView.extend({
    tagName: "ul",
    id: "letters",
    className: "letters-ul",
    template: "#letters-template",
    childView: LetterView,
    remove: function() {
        glossary.off('select:letter');
    },
    initialize: function() {
        this.listenTo(this.collection, "sort", this.renderCollection);
        var self = this;
        glossary.on('select:letter',
            function (thisLetter) {
                // UNSELECT OTHERS
                _.each(self.collection.models, function(otherLetter) {
                    otherLetter.setSelected(false);
                });
                // SELECT THIS ONE
                thisLetter.setSelected(true);
                // RE-RENDER NAV
                self.render();
                // LOAD NEW LETTER INTO MAIN PAGE
                glossary.getRegion("main").currentView.fetchByLetter(thisLetter.get('value'));
            }
        );
    },
    appendHtml: function(collectionView, itemView){
        var $el = collectionView.append(itemView.el);
    }
});

var Category = Backbone.Model.extend({
    initialize: function (options) {
        this.set({selected: options.selected === null ? false : options.selected});
        this.set({ value: options.value });
    },       
    setSelected: function(bool) {
        this.set({ selected: bool });
    },
    isSelected: function() {
        return this.get('selected');
    }
});

var Categories = Backbone.Collection.extend({ model: Category });

var CategoryView = Backbone.Marionette.ItemView.extend({
    template: "#category-template",
    tagName: 'span',    // NOT USED
    className: 'category-holder-span',
    events: {
        'click a': 'select'
    },
    select: function() {
        glossary.trigger('select:category', this.model);
    }
});

var CategoriesView = Backbone.Marionette.CompositeView.extend({
    tagName: "ul",
    id: "categories",
    className: "categories-ul",
    template: "#categories-template",
    childView: CategoryView,
    remove: function() {
        glossary.off('select:category');
    },
    initialize: function() {
        this.listenTo(this.collection, "sort", this.renderCollection);
        var self = this;
        glossary.on('select:category',
            function (thisCategory) {
                // UNSELECT OTHERS
                _.each(self.collection.models, function(otherCategory) {
                    otherCategory.setSelected(false);
                });
                // SELECT THIS ONE
                thisCategory.setSelected(true);
                // RE-RENDER NAV
                self.render();
                // LOAD NEW CATEGORY INTO MAIN PAGE
                glossary.getRegion("main").currentView.fetchByCategory(thisCategory.get('id'));
            }
        );
    },
    appendHtml: function(collectionView, itemView){
        var $el = collectionView.append(itemView.el);
    }
});

var Epoch = Backbone.Model.extend({
    initialize: function (options) {
        this.set({selected: options.selected === null ? false : options.selected});
        this.set({ value: options.value });
    },       
    setSelected: function(bool) {
        this.set({ selected: bool });
    },
    isSelected: function() {
        return this.get('selected');
    }
});

var Epochs = Backbone.Collection.extend({ model: Epoch });

var EpochView = Backbone.Marionette.ItemView.extend({
    template: "#epoch-template",
    tagName: 'li',
    className: 'epoch',
    events: {
        'click a': 'select'
    },
    select: function() {
        glossary.trigger('select:epoch', this.model);
    }
});

var EpochsView = Backbone.Marionette.CompositeView.extend({
    tagName: "ul",
    id: "epochs",
    className: "epochs-ul",
    template: "#epochs-template",
    childView: EpochView,
    remove: function() {
        glossary.off('select:epoch');
    },
    initialize: function() {
        this.listenTo(this.collection, "sort", this.renderCollection);
        var self = this;
        glossary.on('select:epoch',
            function (thisEpoch) {
                // UNSELECT OTHERS
                _.each(self.collection.models, function(otherEpoch) {
                    otherEpoch.setSelected(false);
                });
                // SELECT THIS ONE
                thisEpoch.setSelected(true);
                // RE-RENDER NAV
                self.render();
                // LOAD NEW EPOCH INTO MAIN PAGE
                glossary.getRegion("main").currentView.fetchByEpoch(thisEpoch.get('id'));
            }
        );
    },
    appendHtml: function(collectionView, itemView){
        var $el = collectionView.append(itemView.el);
    }
});

var Novel = Backbone.Model.extend({
    initialize: function (options) {
        this.set({selected: options.selected === null ? false : options.selected});
        this.set({ value: options.value });
    },       
    setSelected: function(bool) {
        this.set({ selected: bool });
    },
    isSelected: function() {
        return this.get('selected');
    }
});

var Novels = Backbone.Collection.extend({ model: Novel });

var NovelView = Backbone.Marionette.ItemView.extend({
    template: "#novel-template",
    tagName: 'li',
    className: 'novel',
    events: {
        'click a': 'select'
    },
    select: function() {
        glossary.trigger('select:novel', this.model);
    }
});

var NovelsView = Backbone.Marionette.CompositeView.extend({
    tagName: "ul",
    id: "novels",
    className: "novels-ul",
    template: "#novels-template",
    childView: NovelView,
    remove: function() {
        glossary.off('select:novel');
    },
    initialize: function() {
        this.listenTo(this.collection, "sort", this.renderCollection);
        var self = this;
        glossary.on('select:novel',
            function (thisNovel) {
                // UNSELECT OTHERS
                _.each(self.collection.models, function(otherNovel) {
                    otherNovel.setSelected(false);
                });
                // SELECT THIS ONE
                thisNovel.setSelected(true);
                // RE-RENDER NAV
                self.render();
                // LOAD NEW NOVEL INTO MAIN PAGE
                glossary.getRegion("main").currentView.fetchByNovel(thisNovel.get('id'));
            }
        );
    },
    appendHtml: function(collectionView, itemView){
        var $el = collectionView.append(itemView.el);
    }
});

var Term = Backbone.Model.extend({});

var Terms = Backbone.Collection.extend({ 
    model: Term,
    addTerm: function(term) {
        var self = this;
        var newTerms = [];
        if (glossary.isInContext(term)) {
            // STRIP OUT EMPTY IF NECESSARY
            newTerms = _.without(self.models, _.findWhere(self.models, { id: null }));
            // REDRAW WITH NEW
            newTerms.push(term);
            self.reset(newTerms);
        }
        // LEAVE ALONE
        glossary.vent.trigger("add-term:success");
    },
    removeTerm: function(term) {
        var self = this;
        var newTerms = _.without(self.models, _.findWhere(self.models, { id: term.get("id") }));
        if (!newTerms || newTerms.length === 0) {
            newTerms[0] = new Term({id:null,eventDate:null,novels:[{id:null,name:'None'}],name: 'No Items Found', definition: 'No Match', categories: [{id:null,name:'Uncategorized',longName:'Uncategorized'}],allCategories: [{id:null,name:'Uncategorized',longName:'Uncategorized'}] });
        }
        self.reset(newTerms);
        glossary.vent.trigger("delete-term:success");
    },
    showTerms: function(terms) {
        var self = this;
        var newTerms = [];
        if (!terms || terms.length === 0) {
            newTerms[0] = new Term({id:null,eventDate:null,novels:[{id:null,name:'None'}],name: 'No Items Found', definition: 'No Match', categories: [{id:null,name:'Uncategorized',longName:'Uncategorized'}], allCategories: [{id:null,name:'Uncategorized',longName:'Uncategorized'}] });
        }
        else {
            _.each(terms,function(newTerm) {
                if (glossary.isInContext(newTerm)) {
                    newTerms[newTerms.length] = newTerm;
                }
            });
        }
        self.reset(newTerms);
    }
});

var myTerms = new Terms([{id:null,eventDate:null,novels:[{id:null,name:'None'}],name: 'Loading Data', definition: 'Data is Loading ....', categories: [{id:null,name:'Uncategorized',longName:'Uncategorized'}], allCategories: [{id:null,name:'Uncategorized',longName:'Uncategorized'}] }]);

var TermView = Backbone.Marionette.ItemView.extend({
    template: "#term-template",
    tagName: 'tr',
    className: 'term',
    modelEvents: {
        'change': 'fieldsChanged'
    },
    fieldsChanged: function() {
        this.render();
    },
    events: {
        'click span.update-term': 'promptUpdate',
        'click span.delete-term': 'promptDelete'
    },
    promptUpdate: function() {
        glossary.trigger('update:term', this.model);
    },
    promptDelete: function() {
        glossary.trigger('delete:term', this.model);
    },
    onRender: function() {
        this.$el.find('a.term-link').linkMe();
    }
});

var TermsView = Backbone.Marionette.CompositeView.extend({
    tagName: "table",
    id: "terms",
    className: "terms-table",
    template: "#terms-template",
    childView: TermView,
    fetchByLetter: function(letter) {
        this.fetch({ url:'/Superiad/Fetch/Terms/ByLetter/' + letter + ".do" }, function(args) { glossary.storeMode(MODES.LETTER,letter); myTerms.showTerms(args);  });
    },
    fetchByCategory: function(categoryID) {
        this.fetch({url:'/Superiad/Fetch/Terms/ByCategory/' + categoryID + ".do"}, function(args) { glossary.storeMode(MODES.CATEGORY,categoryID); myTerms.showTerms(args); });
    },
    fetchByEpoch: function(epochID) {
        this.fetch({url:'/Superiad/Fetch/Terms/ByEpoch/' + epochID + ".do"}, function(args) { glossary.storeMode(MODES.EPOCH,epochID); myTerms.showTerms(args); });
    },
    fetchByNovel: function(novelID) {
        this.fetch({url:'/Superiad/Fetch/Terms/ByNovel/' + novelID + ".do"}, function(args) { glossary.storeMode(MODES.NOVEL,novelID); myTerms.showTerms(args); });
    },
    fetch: function(args, callback) {
        var self = this;
        if (this.loading) return;
        glossary.vent.trigger('search:start');
        this.loading = true;
        args.dataType = 'json';
        args.success = function(res) {
            glossary.vent.trigger('search:stop');
            if (res.length === 0) {
                if (callback) callback([]);
                self.loading = false;
                return [];
            }
            else if (res.length) {
                var terms = [];
                _.each(res, function(term) {
                    if (!term.categories.length) {
                        term.categories = [{id:null,name:'Uncategorized',longName:'Uncategorized'}];
                    }
                    terms.push(
                        new Term(term)
                    );
                });
                if (callback) callback(terms);
                self.loading = false;
                return terms;
            }
            else if (res.error) {
                glossary.vent.trigger("search:error");
                self.loading = false;
            }
        };
        args.error = function() {
            glossary.vent.trigger("search:error");
            self.loading = false;
        };
        $.ajax(args);
    },
    initialize: function() {
        this.listenTo(this.collection, "sort", this.renderCollection);
        this.listenTo(this.collection, "add", this.renderCollection);
        glossary.vent.on("search:start", function() { console.log('Starting a Search!'); });
        glossary.vent.on("search:stop", function() { console.log('Stopping a Search!'); });
        glossary.vent.on("search:error", function() { console.log('Error in Search!'); });
        glossary.vent.on("nav:error", function() { console.log('Error in Nav Lookup!'); });
        glossary.vent.on("delete:error", function() { console.log('Error deleting Term!'); });
        glossary.vent.on("crossReference:error", function() { console.log('Error cross-referencing Terms!'); });
        glossary.vent.on("add-term:success", function() { console.log('Term Added!'); });
        glossary.vent.on("delete-term:success", function() { console.log('Term Deleted!'); });
        this.loading = false;
        $(document).pxTooltip();
        glossary.on('update:term',
            function (thisTerm) {
                var id;
                try {
                    id = thisTerm.get("id");
                }
                catch(e) { }
                if (!!id) {
                    var callback = function(newModel) {
                        thisTerm.set(newModel);
                        if (!glossary.isInContext(thisTerm)) {
                            myTerms.removeTerm(thisTerm);
                        }
                    };
                    submittableDialog('/Superiad/CRUD/PromptUpdate/Term/' + id + '.do','Edit Entry : ' + thisTerm.get('name'), id, { 'callback': callback });
                }
                else {
                    textDialog('This entry is not editable.','Edit Entry');
                }
            }
        );
        glossary.on('delete:term',
            function (thisTerm) {
                var id;
                try {
                    id = thisTerm.get("id");
                }
                catch(e) { }
                if (!!id) {
                    var callback = function() {
                        var ajaxArgs = {
                            url: '/Superiad/CRUD/DoDelete/Term/' + id + '.do',
                            type:'POST',
                            dataType:'json',
                            success: function() { 
                                myTerms.removeTerm(thisTerm);
                            },
                            error: function() {
                                glossary.vent.trigger("delete:error");
                            }
                        };
                        $.ajax(ajaxArgs);
                    }
                    confirmationDialog('Are you sure you wish to delete the term ' + thisTerm.get('name') + '?',
                        'Delete Entry : ' + thisTerm.get('name'),callback);
                }
                else {
                    textDialog('This entry is not deleteable.','Delete Entry');
                }
            }
        );
    },
    onAddChild: function() {
        this.updateCount();
    },
    onRemoveChild: function() {
        this.updateCount();
    },
    onRender: function() {
        if (glossary.isShowing(MODES.NOVEL)) {
            this.$el.find('tr.novels').show();
        }
        else {
            this.$el.find('tr.novels').hide();
        }
        if (glossary.isShowing(MODES.CATEGORY)) {
            this.$el.find('.categories').show();
        }
        else {
            this.$el.find('.categories').hide();
        }
        if (glossary.isShowing(MODES.EPOCH)) {
            this.$el.find('.epochs').show();
        }
        else {
            this.$el.find('.epochs').hide();
        }
        this.updateCount();
    },
    updateCount: function() {
        var matches = 0;
        _.each(this.collection.models, function(model) {
            if (!!model.get("id")) {
                matches++;
            }
        });
        $('.terms-count').text(matches === 1 ? 'There is 1 match!' : ('There are ' + matches + ' matches!'));
    },
    appendHtml: function(collectionView, itemView){
        collectionView.$("tbody").append(itemView.el);
    }
});

var NavView = Backbone.Marionette.ItemView.extend({
    template: "#nav-template",
    tagName: 'div',
    className: 'nav-div',
    events: {
        'click button#mode-letter-button': 'loadLetterNav',
        'click button#mode-category-button': 'loadCategoryNav',
        'click button#mode-epoch-button': 'loadEpochNav',
        'click button#mode-novel-button': 'loadNovelNav',
        'click button#show-category-button': 'toggleShowCategories',
        'click button#show-epoch-button': 'toggleShowEpochs',
        'click button#show-novel-button': 'toggleShowNovels',
        'click button#add-term': 'addTerm',
        'click button#manage-categories': 'manageCategories',
        'click button#cross-reference' : 'crossReference'
    },
   crossReference: function() {
        var ajaxArgs = {
            url: '/Superiad/glossary/crossReference.do',
            type: 'POST',
            dataType: 'json',
            success: function() {
                textDialog('Terms have been successfully cross-referenced!','Success');
            },
            error: function() {
                glossary.vent.trigger("crossReference:error");
            }
        };
        $.ajax(ajaxArgs);
   },
   manageCategories: function() {
        var callback = function(data) {
            /* DO NOTHING */
        };
        submittableDialog('/Superiad/CRUD/PromptManageCategories.form','Manage Categories', 0, { 'callback': callback });
    },
    addTerm: function() {
        var callback = function(data) {
            myTerms.addTerm(new Term(data));
        };
        submittableDialog('/Superiad/CRUD/PromptCreate/Term.do','Create New Standard Term', 0, { 'callback': callback });
    },
    toggleShowNovels: function() {
        this.toggleShowButtons(this.$el.find('div#nav-controls>button#show-novel-button'),glossary.toggleShowing(MODES.NOVEL));
        glossary.getRegion("main").currentView.render();
    },
    toggleShowEpochs: function() {
        this.toggleShowButtons(this.$el.find('div#nav-controls>button#show-epoch-button'),glossary.toggleShowing(MODES.EPOCH));
        glossary.getRegion("main").currentView.render();
    },
    toggleShowCategories: function() {
        this.toggleShowButtons(this.$el.find('div#nav-controls>button#show-category-button'),glossary.toggleShowing(MODES.CATEGORY));
        glossary.getRegion("main").currentView.render();
    },
    toggleShowButtons: function($buttons,bool) {
        if (bool) {
            $buttons.removeClass('link-button-orange').addClass('link-button-green');
        }
        else {
            $buttons.removeClass('link-button-green').addClass('link-button-orange');
        }
    },
    toggleNavOn: function($buttons) {
        $buttons.removeClass('link-button-dkgray').addClass('link-button-green');
    },
    toggleNavOff: function($buttons) {
        $buttons.addClass('link-button-dkgray').removeClass('link-button-green');
    },
    loadLetterNav: function() {
        this.toggleNavOff(this.$el.find('div#nav-modes>button'));
        this.toggleNavOn(this.$el.find('div#nav-modes>button#mode-letter-button'));
        var lettersView = new LettersView({
            collection: LETTERS
        });
        glossary.getRegion("left").show(lettersView);    
        glossary.trigger('select:letter', lettersView.collection.models[0]);
    },
    loadEpochNav: function() {
        this.toggleNavOff(this.$el.find('div#nav-modes>button'));
        this.toggleNavOn(this.$el.find('div#nav-modes>button#mode-epoch-button'));
        var callback = function (epochs) { 
            var epochsView = new EpochsView({
                collection: new Epochs(epochs || { })
            });
            glossary.getRegion("left").show(epochsView);    
            glossary.trigger('select:epoch', epochsView.collection.models[0]);
        };
        this.fetch({ url: '/Superiad/Fetch/Epochs.do' }, callback);
    },
    loadCategoryNav: function() {
        this.toggleNavOff(this.$el.find('div#nav-modes>button'));
        this.toggleNavOn(this.$el.find('div#nav-modes>button#mode-category-button'));
        var callback = function (categories) { 
            var categoriesView = new CategoriesView({
                collection: new Categories(categories || { })
            });
            glossary.getRegion("left").show(categoriesView);    
            glossary.trigger('select:category', categoriesView.collection.models[0] );
        };
        this.fetch({ url: '/Superiad/Fetch/Categories.do' }, callback);
    },
    loadNovelNav: function() {
        this.toggleNavOff(this.$el.find('div#nav-modes>button'));
        this.toggleNavOn(this.$el.find('div#nav-modes>button#mode-novel-button'));
        var callback = function (novels) { 
            var novelsView = new NovelsView({
                collection: new Novels(novels || { })
            });
            glossary.getRegion("left").show(novelsView);    
            glossary.trigger('select:novel', novelsView.collection.models[0] );
        };
        this.fetch({ url: '/Superiad/Fetch/Novels.do' }, callback);
    },
    fetch: function(args, callback) {
        var self = this;
        if (this.loading) return;
        this.loading = true;
        args.dataType = 'json';
        args.success = function(res) {
            if (res.length === 0) {
                if (callback) callback([]);
                self.loading = false;
                return [];
            }
            else if (res.length) {
                if (callback) callback(res);
                self.loading = false;
                return res;
            }
            else if (res.error) {
                glossary.vent.trigger("nav:error");
                self.loading = false;
            }
        };
        args.error = function() {
            glossary.vent.trigger("nav:error");
            self.loading = false;
        };
        $.ajax(args);
    }
});

glossary.addInitializer(function(options){
    var navView = new NavView();
    glossary.getRegion("innerTop").show(navView);
    var lettersView = new LettersView({
        collection: LETTERS
    });
    glossary.getRegion("left").show(lettersView);    
    var termsView = new TermsView({
        collection: myTerms
    });
    glossary.getRegion("main").show(termsView);   
    termsView.fetchByLetter('A');
});

$(document).ready(function(){
    glossary.start();
});

