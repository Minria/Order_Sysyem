版本迭代：



# 1. 项目简介

## 1.1 项目背景

该项目就是为了顾客方便通过手机来进行在线点餐。

## 1.2 模块

对用户的管理

对菜品的管理

对订单的管理

## 1.3 技术栈

MySQL以及JavaJDBC编程

Servlet编程

Http网络

# 2. 需求分析

## 2.1 用户管理

注册、登录、注销

需要区分顾客与商家

## 2.2 菜品管理

能够对菜品进行增删查改

对于顾客：只能查看

对于商家：可以进行增删查改

## 2.3 订单管理

对订单进行增删查改

对于顾客：新增订单以及查看自己过去订单

对于商家：查看所有订单，修改订单状态

# 3. 数据库设计

## 3.1 对于用户需要有

id，用户名，密码，还要有一个区分是商家还是顾客

```sql
create table user (
    userId int primary key auto_increment,
    name varchar(50) unique,
    password varchar(50),
    isAdmin int  -- 是否是管理员, 0 表示不是管理员, 1 表示是管理员
);
```



## 3.2 对于菜品需要有

id，名称，价格

```sql
create table dishes (
    dishId int primary key auto_increment,
    name varchar(50),
    price int  -- 以分为单位, 使用 int 表示. 尽量不要用 float double, 会存在误差
);
```



需要注意的价格为多少分，不可用float以及double，由于IEEE754格式，导致浮点相加容易出现错误

## 3.3 对于订单管理

我们需要有两张表，一张关联用户订单，一张关联订单和菜品

对于用户订单表

订单id，用户id，下单时间，是否完成

```sql
create table order_user (
    orderId int primary key auto_increment,
    userId int,    -- 这个用户 id 需要和 user 表中的 userId 具有关联关系
    time datetime, -- 下单时间
    isDone int,    -- 1 表示订单完结, 0 表示订单未完结.
    foreign key(userId) references user(userId)
);
```

对于订单菜品表

订单id，菜品id

```sql
create table order_dish (
    orderId int,   -- orderId 也和 order_user 表的 orderId 字段有外键关系
    dishId int,    -- dishId 也和 dishes 表中的 dishId 存在外键关系
    foreign key(orderId) references order_user(orderId),
    foreign key(dishId) references dishes(dishId)
);
```

# 4. 数据库操作

## 4.1 对用户操作

```java
public static void add(User user);
public static void deleteUser(int id);
public static User seleteById(int id);
```

## 4.2 对菜品的操作

```java
public static void add(Dish dish);
public static void delete(int id);
public static List<Dish> seleteAll();
public static Dish seleteById(int id);
```

# 5. 前后端API的约定

## 5.1 用户操作

用户注册

```
POST/register
{
	name:xxx,
	password:xxx
}
HTTP/1.1 200 OK
{
	ok:1,
	reason:xxx
}
```

用户登录

```
POST/login
{
	name:xxx,
	password:xxx
}
HTTP/1.1 200 OK
{
	ok:1,//1表示成功
	reason:xxx,//失败的时候返回原因
	name:xxx,
	isAdmin:0 //0表示普通用户，1表示管理员
}
```

检查登录状态

```
GET/login
{
}
{
	ok:1,
	reason:xxx,
	name:xxx,
	isAdmin:0
}
```

用户注销

```
GET/logout
{
}
{
	ok:1,
	reason:xxx
}
```

## 5.2 对菜品的管理

 新增菜品

需要验证管理员身份

```
POST/dish
{
	name:xxx,
	price:xxx,
}
{
	ok:1,
	reason:xxx
}
```

删除菜品

需要验证管理员身份

```
DELETE/dish?dishId=xxx
{
}
{
	ok:1,
	reason:xxx
}
```

查看所有菜品

```
GET/dish
{
}
{
	{
		dishId:xxx,
		name:xxx,
		price:xxx
	},{
		dishId:xxx,
		name:xxx,
		price:xxx	
	}
}
```

## 5.3 订单管理

新增订单

```
POST/order
{
	[1,2,3,4]
}
{
	ok:1,
	reason:xxx
}
```

查看订单

根据身份来判断操作

```
GET/order
{
}
{
	{
		orderId:xxx,
		userId:xxx,
		time:xxx,
		isDone:xxx,
	}
}
```

查看指定订单详情

```
GET/order?orderId=xxx
{
}
{
	orderId:xxx,
	userId:xxx,
	time:xxx,
	isDone:xxx,
	dishes:xxx
}
```

修改订单状态

```	
PUT/order?orderId=xxx&isDone=1
{
}
{
	ok:1,
	reason:xxx
}
```

