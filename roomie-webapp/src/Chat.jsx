import "./Chat.css"
import { useState, useEffect, useRef } from "react"
import PropTypes from 'prop-types';

function Chat({ selectedChat }) {
    const [text, setText] = useState('')
    const [conversation, setConversation] = useState([])
    const [messages, setMessages] = useState([])

    const messageAreaRef = useRef(null)



    let name = selectedChat ? `${selectedChat[0]} ${selectedChat[1]}` : "Unknown User";

    useEffect(() => {
        // reset chat for new selected chat here --> for now just clear conversation area
        setConversation([])

    }, [selectedChat])

    useEffect(() => {
        scrollToBottom()
    }, [conversation])

    function sendMessage() {
        if(!selectedChat) return

        const newMessage = {
            type: "sent",
            text: text
        }

        setMessages((prevMessages) => [...prevMessages, newMessage])

        const sendMessageData = async () => {
            try {
                const messageData = JSON.stringify({
                    token: localStorage.getItem("token"),
                    groupchat_id: selectedChat[2],
                    message: text
                })
    
                const response = await fetch("https://roomie.ddns.net:8080/chat/sendMessage", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: messageData,
                });
    
                if (!response.ok) {
                    throw new Error("Message Data Sending failed. Please try again.");
                }
    
            } catch (error) {
                console.error("HERE we are", error)
            }
        }
        sendMessageData()
        setText("")
    }

    function handleKeyPress(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault(); // Prevents default behavior (new line)
            sendMessage(e);
            e.target.style.height = "50px"
        }
    }

    const scrollToBottom = () => {
        if (messageAreaRef.current) {
            messageAreaRef.current.scrollTop = messageAreaRef.current.scrollHeight
        }
    }

    return (
        <div className="holdChat">
            <div className="messageArea" ref={messageAreaRef}>
                <h5 className="chatNote" >You are chatting with {name}</h5>
                {/* {conversation.map((item, index) => (
                    item.type === 'message' ? (
                        <p key={index} className="right bubble">{item.content}</p>
                    ) : (
                        <p key={index} className="left bubble">{item.content}</p>
                    )
                ))} */}

                {messages.map((msg, index) => {
                    <div key={index} className="message">
                        {msg.text}
                    </div>
                })}

            </div>
            <div className="messageInput">
            <textarea
                value={text}
                onChange={(e) => setText(e.target.value)}
                onKeyDown={handleKeyPress}
                onInput={(e) => {
                    e.target.style.height = "50px"; // Reset height to auto to recalculate
                    e.target.style.height = `${e.target.scrollHeight}px`; // Set new height
                }}
                style={{ resize: "none", overflowY: "hidden" }} // Prevent manual resizing
                placeholder="Type a message..."
                className="messageTextBox form-control"
                />
                {/* <textarea value={text} onChange={(e) => setText(e.target.value)} onKeyDown={handleKeyPress} style={{resize: "none"}} className="form-control" type="text"/> */}
                <button onClick={sendMessage} className=" sendButton">
                    <i className="bi bi-send"/>
                </button>
            </div>
            
        </div>
    )

}
Chat.propTypes = {
    selectedChat: PropTypes.object
};

export default Chat                                        