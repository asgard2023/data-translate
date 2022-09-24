var rowIds = '';
var transTypeCode = 'DflUserPo';

$(function() {
    var transTypeDistsParam=getQueryString('transTypeDists');
	//方便演示测试，支持参数输入
    if(transTypeDistsParam){
        transTypeDist=transTypeDistsParam;
    }
    else{
        trans_getTypeDists();
    }
	//方便演示测试，支持参数输入
    //自动获取对象的翻译属性，以覆盖transFields
	var transFieldParam=getQueryString('transFields');
	if(transFieldParam){
		transFields = transFieldParam;
	}
	var beforeDay=100;
	initStartEndTime(beforeDay);
	pageInit();
});
function doSearch() {
	var queryObj = formToJson('searchForm');
    $("#table_list_1").jqGrid("setGridParam", { postData:  queryObj, page: 1}).trigger("reloadGrid");
}
var editOption =
	{
		url: "/dflUser/update",
		recreateForm: true,
		beforeSubmit: function( postdata, form , oper) {
			return [true,''];
		},
		closeAfterEdit: true,
		errorTextFormat: function (data) {
			return 'Error: ' + data.responseText
		},
		afterSubmit: function(response, postdata)  {
			var result = eval('(' + response.responseText + ')');
			if(result.success==true) {
				trans_saveLocalLanguage(result.data, postdata, transFields);
				swal({title:"消息提示",text:"修改成功"});
				return [true,''];
			} else {
				return [false, '修改失败，' + result.errorMsg];
			}
		}
	};
function pageInit() {
	var queryObj = formToJson('searchForm');
	$("#table_list_1").jqGrid({
		url:  '/dflUser/list?transTypeDist='+transTypeDist,
        mtype: 'POST',
		datatype: "json",
		styleUI: 'Bootstrap',
		postData: queryObj,
		colNames: ['id', '昵称','昵称en','昵称Tw','昵称Ja', '个人描述','个人描述En','个人描述Tw','个人描述Ja', '用户名', '电话', 'sys_type', '邮箱', '密码', '是否删除', '状态:是否有效0无效，1有效', 'remark', 'create_time', 'modify_time', 'create_user', 'modify_user', 'register_ip'],
        colModel: [{name: 'id', index: 'id', editable:true, hidden: true,key:true},
            {name: 'nickname', index: 'nickname',editable:true, width: 100, editrules:{required:true}},
            {name: 'nicknameEn', index: 'nicknameEn',editable:true, width: 100, editrules:{required:true}},
            {name: 'nicknameTw', index: 'nicknameTw',editable:true, width: 100, editrules:{required:true}},
            {name: 'nicknameJa', index: 'nicknameJa',editable:true, width: 100, editrules:{required:true}},
			{name: 'descs', index: 'descs',editable:true, width: 100, editrules:{required:true}},
			{name: 'descsEn', index: 'descsEn',editable:true, width: 100, editrules:{required:true}},
			{name: 'descsTw', index: 'descsTw',editable:true, width: 100, editrules:{required:true}},
			{name: 'descsJa', index: 'descsJa',editable:true, width: 100, editrules:{required:true}},
            {name: 'username', index: 'username',editable:true, width: 100, editrules:{required:true}},
            {name: 'telephone', index: 'telephone',editable:true, width: 100},
			{name: 'sysType', index: 'sysType',editable:true, width: 100},
            {name: 'email', index: 'email',editable:true, width: 100},
            {name: 'pwd', index: 'pwd',editable:true, width: 100},
            {name: 'ifDel', index: 'ifDel',editable:false, hidden: true, width: 100, editrules:{required:true}},
            {name: 'status', index: 'status',editable:true, width: 100, formatter: 'select',
		formatoptions: {value: dict_status},
		edittype: 'select',
		editoptions: {value: dict_status}, editrules:{required:true}},
            {name: 'remark', index: 'remark',editable:true, width: 100},
            {name: 'createTime', index: 'createTime',editable:false, width: 100},
            {name: 'modifyTime', index: 'modifyTime',editable:false, width: 100},
            {name: 'createUser', index: 'createUser',editable:false, width: 100},
            {name: 'modifyUser', index: 'modifyUser',editable:false, width: 100},
            {name: 'registerIp', index: 'registerIp',editable:false, width: 100}
	    ],
		rowNum: 10,
		rows: 'abc',
		rowList:[10,50,100],
		prmNames: {
			rows: 'pageSize',
			page: 'pageNum',
			sort: 'orderBy',
			order: 'order'
		},
		jsonReader: {
			root: "list",    // json中代表实际模型数据的入口
			page: "pageNum",    // json中代表当前页码的数据
			total: "pages",    // json中代表页码总数的数据
			records: "total", // json中代表数据行总数的数据
			dicts: 'dicts'
		 },
		autowidth:true,
		shrinkToFit:true,
		pager: '#pager_list_1',
		//sortname: '',
		viewrecords: true,
		sortorder: "desc",
		hidegrid: false,
		loadComplete: function (r){
            var list=r.list;
			var dicts=r.dicts;
			var typeInfo;
			if(dicts){
				typeInfo=dicts.typeInfo;
			}
			trans_jqGridCells('table_list_1', list, transTypeCode, typeInfo);
		},
		onSortCol: function(name, index) {
		  //alert("Column Name: " + name + " Column Index: " + index);
		},
		ondblClickRow: function(rowid) {
			$("#table_list_1").editGridRow(rowid,editOption);
		},
		caption: "",
		//height: 400,
		height: 'auto',
		editOptions: {
			top: 50, left: "100", width: 800
			,closeOnEscape: true, afterSubmit: function(response, postData) {
				alert(123);

			}
		}
	});
	$("#table_list_1").jqGrid('navGrid', "#pager_list_1",
		{
		  edit: true,
		  add: true,
		  del: true,
		  search: true,
		  refresh: false
		},
		editOption,
		{
			//add
			url: "/dflUser/save",
			closeAfterAdd: true,
			recreateForm: true,
			errorTextFormat: function (data) {
				return 'Error: ' + data.responseText
			},
			afterSubmit: function(response, postdata)  {
				var result = eval('(' + response.responseText + ')');
				if(result.success==true) {
					trans_saveLocalLanguage(result.data, postdata, transFields);
					swal({title:"消息提示",text:"添加成功"});
					return [true,''];
				} else {
					return [false, '添加失败，' + result.errorMsg];
				}
			}
		},
		{
			//delete
			url:"/dflUser/delete",
			afterSubmit: function(response, postdata)  {
				var result = eval('(' + response.responseText + ')');
				if(result.success==true) {
					swal({title:"消息提示",text:"删除成功"});
					return [true,''];
				} else {
					return [false, '删除失败，' + result.errorMsg];
				}
			}
		},
		{
			//search
			closeAfterSearch: true,
			multipleSearch: true
		}
	);

	$("#btnSearch").click(function(){
	  doSearch();
	});
}