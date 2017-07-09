# Github API

Github的开放API是非常规范的RESTful，所以研究一下  
API文档地址：https://developer.github.com/v3/
--------------------------------


## 1 Hello World

测试都是用curl来测，hello world是这个： 
```
curl https://api.github.com/zen

返回值是一个随机的文本
```

大体说下惯例，以获取用户profile为例
```
加上参数i，打印完整http响应

curl -i https://api.github.com/users/cowthan

返回：
HTTP/1.1 200 OK
Server: GitHub.com
Date: Sun, 11 Nov 2012 18:43:28 GMT
Content-Type: application/json; charset=utf-8
Connection: keep-alive
Status: 200 OK
ETag: "bfd85cbf23ac0b0c8a29bee02e7117c6"
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 57
X-RateLimit-Reset: 1352660008
X-GitHub-Media-Type: github.v3
Vary: Accept
Cache-Control: public, max-age=60, s-maxage=60
X-Content-Type-Options: nosniff
Content-Length: 692
Last-Modified: Tue, 30 Oct 2012 18:58:42 GMT

{
  "login": "cowthan",
  "id": 5715956,
  "avatar_url": "https://avatars.githubusercontent.com/u/5715956?v=3",
  "gravatar_id": "",
  "url": "https://api.github.com/users/cowthan",
  "html_url": "https://github.com/cowthan",
  "followers_url": "https://api.github.com/users/cowthan/followers",
  "following_url": "https://api.github.com/users/cowthan/following{/other_user}",
  "gists_url": "https://api.github.com/users/cowthan/gists{/gist_id}",
  "starred_url": "https://api.github.com/users/cowthan/starred{/owner}{/repo}",
  "subscriptions_url": "https://api.github.com/users/cowthan/subscriptions",
  "organizations_url": "https://api.github.com/users/cowthan/orgs",
  "repos_url": "https://api.github.com/users/cowthan/repos",
  "events_url": "https://api.github.com/users/cowthan/events{/privacy}",
  "received_events_url": "https://api.github.com/users/cowthan/received_events",
  "type": "User",
  "site_admin": false,
  "name": "seven",
  "company": null,
  "blog": null,
  "location": null,
  "email": "cowthan@163.com",
  "hireable": null,
  "bio": "每个人都觉得你很酷，因为你！就！是！那！么！酷！",
  "public_repos": 36,
  "public_gists": 1,
  "followers": 7,
  "following": 0,
  "created_at": "2013-10-18T06:42:28Z",
  "updated_at": "2016-12-31T18:12:18Z"
}

```

### 1 关于header

* header：
    * `Content-Typ`e是`application/json`
    * `X-`开头的是自定义的header，不在http规范里
        * `X-GitHub-Media-Type` = github.v3：MediaType和api版本
        * `X-RateLimit-Limit` and `X-RateLimit-Remaining`：一段rolling time内，一般一个小时，可以请求的次数和剩余次数
            * Unauthenticated的客户端请求限制是60次/小时
            * authenticated的客户端请求限制是5000次/小时

### 2 关于Authentication

`方式1：Basic`  
```
curl -i -u your_username https://api.github.com/users/defunkt
Enter host password for user your_username: 命令行会提示你输入密码
或者直接输入密码：
curl -i -u "username:password" https://api.github.com/users/defunkt
```
如果只想提高访问次数的限制，可以用这个方式授权，但如果想传一些重要信息，则不能光这么干  
而且任何想通过授权看你profile的客户端，你都得公开username和password，这不太合理

`方式2：Two-factor authentication`  
如果开启了Two-factor authentication，则未授权的请求会返回`401 Unauthorized`  
解决方式就是使用OAuth token，而不能使用Basic Authentication 
授权之后，同样的接口，返回的字段可能更多（包含了授权之后才能看到的non-public information）

`方式3：OAuth`
通过Token进行授权   
* Token的两个好处：
    * Revokable access: users can revoke authorization to third party apps at any time
    * Limited access: users can review the specific access that a token will provide before authorizing a third party app
* 怎么授权
    * 其实就是常见的通过一个第三方应用打开github授权页面，你登录授权之后，第三方应用就可以得到一个token
        * 这个就是github文档里描述的web flow
    * 也可以在github个人主页里设置personal access token，直接把token给别人
        * 这个方式可以选择token对应的权限
        * 下面给个图  
        
![](./img/a1.png)

* 通过api来请求授权：  
    * 1 curl -i -u your_username -d '{"scopes": ["repo", "user"], "note": "getting-started"}' https://api.github.com/authorizations
        * -d表示这是个post请求，后面的json就是post的请求体
        * 请求体里的scopes或者access levels表示：表示请求的token对应的权限，具体权限看上图
            * 具体的scope看文档：https://developer.github.com/v3/oauth/#scopes
    * 2 不要请求太多权限，只请求你的应用所必需的就行，避免吓着用户
    * 3 返回201表示成功，下面是响应的内容，包含具体的token信息
```
HTTP/1.1 201 Created
Location: https://api.github.com/authorizations/2
Content-Length: 384

{
  "scopes": [
    "repo",
   "user"
 ],
 "token": "5199831f4dd3b79e7c5b7e0ebe75d67aa66e79d4",
 "updated_at": "2012-11-14T14:04:24Z",
 "url": "https://api.github.com/authorizations/2",
 "app": {
   "url": "https://developer.github.com/v3/oauth/#oauth-authorizations-api",
   "name": "GitHub API"
 },
 "created_at": "2012-11-14T14:04:24Z",
 "note_url": null,
 "id": 2,
  "note": "getting-started"
}
```

这一请求有可能返回`401 Unauthorized`，还是上面说的`two-factor authentication`  
为了绕过这个问题，可以提供一个2FA OTP code，通过heaer里的X-GitHub-OTP字段
```
curl -i -u your_username -H "X-GitHub-OTP: your_2fa_OTP_code" -d '{"scopes": ["repo", "user"], "note": "getting-started"}' https://api.github.com/authorizations
```

还有一个注意点，没看明白，直接拷来原文：  
```
If you enabled 2FA with a mobile application, go ahead and get an OTP code from your one-time password application 
on your phone. If you enabled 2FA with text messages, you'll receive an SMS with your OTP code after making a 
request to this endpoint.
```

token的用法：  
```
curl -i -H 'Authorization: token 5199831f4dd3b79e7c5b7e0ebe75d67aa66e79d4' \
    https://api.github.com/user
Treat OAuth tokens like passwords! D
```

* 特别注意：
    * token就类似于username和password，不能交给别人
    * 也不能存在不安全的地方

### 3 缓存

返回304，表示Not Midified，也不会计入rate limits里，什么时候才会返回304呢

看一个实际例子：
```
第一次请求profile：
curl -i https://api.github.com/users/defunkt

HTTP/1.1 200 OK
ETag: "bfd85cbf23ac0b0c8a29bee02e7117c6"

第二次请求profile时，把ETag的值传入request header的If-None-Match里
curl -i -H 'If-None-Match: "bfd85cbf23ac0b0c8a29bee02e7117c6"' \
   https://api.github.com/users/defunkt

HTTP/1.1 304 Not Modified

ETag就是一次响应的fingerprint
```

## 错误码总结

```
404：接口不存在，查的主体不存在，如查用户profile，用户不存在，查repo的issue，repo不存在
401：如果开启了Two-factor authentication，则未授权的请求会返回`401 Unauthorized`

301：永久重定向
302：临时重定向
303：临时重定向
```


## 用户接口


授权之后，同样的接口，返回的字段可能更多（包含了授权之后才能看到的non-public information）

### 1 获取用户profile

```
https://api.github.com/users/cowthan

返回：body
{
  "login": "cowthan",
  "id": 5715956,
  "avatar_url": "https://avatars.githubusercontent.com/u/5715956?v=3",
  "gravatar_id": "",
  "url": "https://api.github.com/users/cowthan",
  "html_url": "https://github.com/cowthan",
  "followers_url": "https://api.github.com/users/cowthan/followers",
  "following_url": "https://api.github.com/users/cowthan/following{/other_user}",
  "gists_url": "https://api.github.com/users/cowthan/gists{/gist_id}",
  "starred_url": "https://api.github.com/users/cowthan/starred{/owner}{/repo}",
  "subscriptions_url": "https://api.github.com/users/cowthan/subscriptions",
  "organizations_url": "https://api.github.com/users/cowthan/orgs",
  "repos_url": "https://api.github.com/users/cowthan/repos",
  "events_url": "https://api.github.com/users/cowthan/events{/privacy}",
  "received_events_url": "https://api.github.com/users/cowthan/received_events",
  "type": "User",
  "site_admin": false,
  "name": "seven",
  "company": null,
  "blog": null,
  "location": null,
  "email": "cowthan@163.com",
  "hireable": null,
  "bio": "每个人都觉得你很酷，因为你！就！是！那！么！酷！",
  "public_repos": 36,
  "public_gists": 1,
  "followers": 7,
  "following": 0,
  "created_at": "2013-10-18T06:42:28Z",
  "updated_at": "2016-12-31T18:12:18Z"
}

```

## Repositories接口

github的repo分几张情况：public和private  
如果用Basic Authentication，会返回这个用户所有在gihub.com上 has aceess to see的项目  
如果用OAuth授权，返回会根据token对应的repo scope来  
下面几个查询接口都支持type参数：https://api.github.com/users/technoweenie/repos?type=owner  


--------------------------------------------
列出token对应的用户的repo列表：
```
curl -i -H 'Authorization: token 5199831f4dd3b79e7c5b7e0ebe75d67aa66e79d4' https://api.github.com/user/repos
```

--------------------------------------------
列出指定用户的repo列表：
```
curl -i https://api.github.com/users/cowthan/repos
```

-------------------------------------------
列出organiaton的repo列表：
```
curl -i https://api.github.com/orgs/Craftspeople/repos
```

-------------------------------------------
repo详情：
```
curl -i https://api.github.com/repos/twbs/bootstrap

/repos/{username}/{repo_name}
```

--------------------------------------------
创建repo：
```
curl -i -H 'Authorization: token 5199831f4dd3b79e7c5b7e0ebe75d67aa66e79d4' \
    -d '{ \
        "name": "blog", \
        "auto_init": true, \
        "private": false, \
        "gitignore_template": "nanoc" \
      }' \
    https://api.github.com/user/repos

也可以是：https://api.github.com//orgs/<org_name>/repos，表示在organization下创建项目
```
post请求，请求体是-d后面的json

然后就会生成这么一个Project：https://github.com/<your_username>/blog  


## Issues接口

issues接口可以查看和提交issues，你可以通过这个api给你的团队创建一个专用的issues处理页面

----------------------------------------------
查看所有你的issues
```
curl -i -H 'Authorization: token 5199831f4dd3b79e7c5b7e0ebe75d67aa66e79d4' https://api.github.com/issues
```

-----------------------------------------------
查看一个repo的issues
```
/repos/:owner/:repo/issues

curl -i https://api.github.com/repos/rails/rails/issues
```

------------------------------------------------
查看指定organization的issues
```
curl -i -H 'Authorization: token 5199831f4dd3b79e7c5b7e0ebe75d67aa66e79d4' https://api.github.com/orgs/rails/issues
```