(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-05b65032"],{"7a22":function(e,t,n){"use strict";n.r(t);var r=n("7a23"),a={style:{background:"#fff",padding:"5px 12px"}},o={style:{"margin-bottom":"20px"}},c=Object(r["i"])("p",{style:{"font-weight":"bold","font-size":"15px"}},"个人信息",-1),i=Object(r["i"])("span",{style:{width:"100px",display:"inline-block"}},"用户名",-1),u={style:{"margin-bottom":"20px"}},s=Object(r["i"])("p",{style:{"font-weight":"bold","font-size":"15px"}},"版本信息",-1),l=Object(r["i"])("span",{style:{width:"100px",display:"inline-block"}},"链版本",-1),d=Object(r["i"])("span",{style:{width:"100px",display:"inline-block"}},"兼容版本",-1),p=Object(r["i"])("span",{style:{width:"100px",display:"inline-block"}},"WeBASE版本",-1),b=Object(r["h"])("退出");function f(e,t,n,f,h,j){var O=Object(r["E"])("van-button");return Object(r["x"])(),Object(r["f"])("div",a,[Object(r["i"])("div",o,[c,Object(r["i"])("p",null,[i,Object(r["i"])("span",null,Object(r["H"])(f.user),1)])]),Object(r["i"])("div",u,[s,Object(r["i"])("p",null,[l,Object(r["i"])("span",null,Object(r["H"])(f.clientVersion),1)]),Object(r["i"])("p",null,[d,Object(r["i"])("span",null,Object(r["H"])(f.supportVersion),1)]),Object(r["i"])("p",null,[p,Object(r["i"])("span",null,Object(r["H"])(f.webaseVersion),1)])]),Object(r["i"])("div",null,[Object(r["i"])(O,{block:"",type:"primary",onClick:f.handleLoginOut},{default:Object(r["M"])((function(){return[b]})),_:1},8,["onClick"])])])}var h=n("1da1"),j=(n("96cf"),n("7ded")),O=n("6c02"),g={name:"Set",setup:function(){var e=Object(O["e"])(),t=sessionStorage.getItem("user"),n=Object(r["C"])(""),a=Object(r["C"])(""),o=Object(r["C"])(""),c=function(){var e=Object(h["a"])(regeneratorRuntime.mark((function e(){var t,r;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,Object(j["b"])();case 2:t=e.sent,r=t.data,n.value=r;case 5:case"end":return e.stop()}}),e)})));return function(){return e.apply(this,arguments)}}(),i=function(){var t=Object(h["a"])(regeneratorRuntime.mark((function t(){var n;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(j["e"])();case 2:n=t.sent,n.data,e.push({path:"/login"});case 5:case"end":return t.stop()}}),t)})));return function(){return t.apply(this,arguments)}}(),u=function(){var e=Object(h["a"])(regeneratorRuntime.mark((function e(){var t,n;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,Object(j["a"])();case 2:t=e.sent,n=t.data,0===n.code&&n.data.length&&(a.value=n.data[0]["clientVersion"],o.value=n.data[0]["supportVersion"]);case 5:case"end":return e.stop()}}),e)})));return function(){return e.apply(this,arguments)}}();Object(r["u"])((function(){c(),u()}));var s=function(){i()};return{handleLoginOut:s,webaseVersion:n,clientVersion:a,supportVersion:o,user:t}}};g.render=f;t["default"]=g},"7ded":function(e,t,n){"use strict";n.d(t,"c",(function(){return c})),n.d(t,"d",(function(){return i})),n.d(t,"e",(function(){return u})),n.d(t,"b",(function(){return s})),n.d(t,"a",(function(){return l}));var r=n("b775"),a=n("4328"),o=n.n(a);function c(){return Object(r["a"])({url:"account/pictureCheckCode",method:"get"})}function i(e,t,n){return Object(r["b"])({url:"/account/login?checkCode=".concat(t),method:"post",data:o.a.stringify(e),headers:{"Content-Type":"application/x-www-form-urlencoded",token:n}})}function u(){return Object(r["a"])({url:"/account/logout",method:"get",headers:{AuthorizationToken:"Token "+localStorage.getItem("token")||!1}})}function s(){return Object(r["a"])({url:"/version",method:"get",responseType:"text",headers:{AuthorizationToken:"Token "+localStorage.getItem("token")||!1}})}function l(e){return Object(r["a"])({url:"/front/find",method:"get",params:e,headers:{AuthorizationToken:"Token "+localStorage.getItem("token")||!1}})}},b775:function(e,t,n){"use strict";n.d(t,"b",(function(){return i})),n.d(t,"a",(function(){return u}));n("caad"),n("2532"),n("d3b7");var r=n("bc3a"),a=n.n(r),o=n("a18c"),c=a.a.create({baseURL:"/mgr/WeBASE-Node-Manager/",timeout:6e4});c.defaults.headers.post["X-Requested-With"]="XMLHttpRequest",c.defaults.responseType="json",c.defaults.validateStatus=function(){return!0},c.interceptors.response.use((function(e){return e.data&&302e3===e.data.code&&o["a"].push({path:"/login",query:{redirect:o["a"].currentRoute.fullPath}}),!e.data||202052!==e.data.code&&202053!==e.data.code||o["a"].push({path:"/login"}),e}),(function(e){return"Error: Network Error"==e&&o["a"].push({path:"/login"}),e.message.includes("timeout")&&("en"===localStorage.getItem("lang")?e.data="Timeout":e.data="请求超时"),Promise.reject(e)}));function i(e){return new Promise((function(t,n){c(e).then((function(e){t(e)})).catch((function(e){n(e)}))}))}function u(e){return new Promise((function(t,n){c(e).then((function(e){t(e)})).catch((function(e){n(e)}))}))}}}]);