# simple-chat-server-with-client
a very simple java socket chat sever and a WPF client

using Oracle data base , or you can modify that very easily

tables are in the client repository

register is not available (you may implement that yourself),or you can manually add an account with a password to the 

'USER' table

and most significantly ADD A BUDDY PAIR TO THE 'BUDDY' TABLE if you want to add a buddy

insert like this:

insert (account1,account2) into 'BUDDY' && insert (account2,account1) into 'BUDDY'

it still has several bugs dealing with the logging messages,you can fix that if you are interested

client @ https://github.com/lwshowl/WPF-chat-client-with-server
