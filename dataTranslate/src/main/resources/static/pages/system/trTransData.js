$('#search-btn').click(function () {
    doSearch();
});
$('#reset-btn').click(function () {
    $('#search-form')[0].reset();
});
var typeId;
$(function () {
    var beforeDay = 7;
    initStartEndTime(beforeDay)
    doSearch();
});

function doSearch() {
    var url = "/dflsystem/trTransData/list2";
    var jsonParam = $('#search-form').serializeJson();
    $('#dg').datagrid({headers: app.headers, url: url, queryParams: jsonParam})
}


function doSearchReload() {
    var jsonParam = $('#search-form').serializeJson();
    $('#dg').datagrid('reload', jsonParam);
}

function onDblClick(rowIndex, rowData) {
    //alert('---onDblClick--rowIndex='+rowIndex+' row.id='+rowData.id);
    onEdit();
}

function iframeCenterSearch(options) {
    console.log('----iframeCenterSearch--' + options);
    if (typeId != options.typeId) {
        typeId = options.typeId;
        $('#query_transTypeId').val(typeId);
        doSearch();
    }
}

function onClick(rowIndex, rowData) {

}

function listenerName(ex) {
    if (ex.keyCode == 13) {
        doSearch();
    }
}

$('#query_code').keydown(listenerName);
$('#query_name').keydown(listenerName);

var url;

function onAdd() {
    $('#dlg').dialog('open').dialog('setTitle', 'New DflRole');
    $('#fm').form('clear');
    $('#dflRole_status').combobox('select', '1');
    //$("#dflRole_remark").val('test');
}

function getRowData(entityName, row) {
    var tmp;
    var obj = {}
    obj[entityName] = {};
    for (i in row) {
        tmp = row[i];
        obj[i] = tmp;
    }
    return obj;
}

function onEdit() {
    var row = $('#dg').datagrid('getSelected');
    if (row) {
        var entityName = 'dflRole';
        $('#fm').form('clear');
        var obj = getRowData(entityName, row);
        $('#dlg').dialog('open').dialog('setTitle', 'Edit DflRole');
        $('#fm').form('load', obj);
    }

}

function onSave() {
    var row = $('#dg').datagrid('getSelected');
    if (row) {
        if (row.canModify == 0) {
            $.messager.show({
                title: 'Error',
                msg: '不可修改，禁止操作'
            });
            return;
        }
    }

    var url = '/dflsystem/trTransData/save';
    var jsonParam = $('#fm').serializeJson();
    if ($('#fm').form('validate')) {
        $.ajax({
            method: 'post',
            url: url,
            data: jsonParam,
            headers: app.headers,
            dataType: 'json',
            success: function (result) {
                if (result.success) {
                    $('#dlg').dialog('close');        // close the dialog
                    $.messager.show({
                        title: '系统消息',
                        msg: result.errorMsg
                    });
                    doSearchReload();    // reload the user data
                } else {
                    $.messager.show({
                        title: '错误消息:' + result.resultCode,
                        msg: result.errorMsg
                    });
                }
            },
            error: function (d, e) {
                $.messager.show({
                    title: e,
                    msg: d.responseJSON.errorMsg,
                    timeout: 3000,
                    showType: 'slide'
                });
            }
        });
    }
}

function onDestroy() {
    var row = $('#dg').datagrid('getSelected');
    if (row) {
        $.messager.confirm('删除确认', '你确定删除此记录?', function (r) {
            if (r) {
                $.ajax({
                    type: 'post',
                    data: {id: row.id},
                    headers: app.headers,
                    dataType: 'json',
                    url: '/dflsystem/trTransData/delete',
                    success: function (data, textStatus, jqXHR) {
                        if (data.success) {
                            $.messager.show({    // show error message
                                title: '系统消息',
                                msg: '删除成功'
                            });
                            doSearchReload();    // reload the user data
                        } else {
                            $.messager.show({    // show error message
                                title: 'Error',
                                msg: data.errorMsg
                            });
                        }
                    },
                    error(d, e) {
                        $.messager.show({    // show error message
                            title: 'Error',
                            msg: d.responseJSON.errorMsg
                        });
                    }
                });
            }
        });
    } else {
        $.messager.show({
            title: '系统提示',
            msg: '请先选择要删除的记录'
        });
    }
}

function transRepeat() {
    if (!typeId) {
        $.messager.show({
            title: '系统提示',
            msg: '请先点击左边的数据类型'
        });
        return;
    }

    var url = 'transDataRepeat.html?id=' + typeId;
    $("#dialog").dialog({
        title: '重复数据(为空表示正常，即没有数据重复)',
        width: 600,
        height: 400,
        modal: false,
        content: "<iframe scrolling='auto' frameborder='0' src='" + url + "' style='width:100%; height:100%; display:block;'></iframe>"
    });
    $("#dialog").dialog("open"); // 打开dialog
}

function transCount() {
    if (!typeId) {
        $.messager.show({
            title: '系统提示',
            msg: '请先点击左边的数据类型'
        });
        return;
    }

    var url = 'transDataCount.html?id=' + typeId;
    $("#dialog").dialog({
        title: '翻译数统计',
        width: 600,
        height: 400,
        modal: false,
        content: "<iframe scrolling='auto' frameborder='0' src='" + url + "' style='width:100%; height:100%; display:block;'></iframe>"
    });
    $("#dialog").dialog("open"); // 打开dialog
}