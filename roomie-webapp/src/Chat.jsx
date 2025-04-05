import "./Chat.css"
import { useState, useEffect, useRef } from "react"
import PropTypes from 'prop-types';

function Chat({ selectedChat }) {
    const [text, setText] = useState('')
    const [messages, setMessages] = useState([])
    const [chatHistory, setChatHistory] = useState([])
    const [requestStatus, setRequestStatus] = useState("")

    const messageAreaRef = useRef(null)

    let name = selectedChat ? `${selectedChat[0]} ${selectedChat[1]}` : "...";

    useEffect(() => {
        setMessages([])
        if(selectedChat) {
            const fetchChatHistory = async () => {
                const history = await getChatHistory(selectedChat[2])
                setChatHistory(history)
            }
            fetchChatHistory()
            
            const fetchRequestStatus = async() => {
                const status = await getRoommateRequestStatus()
                setRequestStatus(status)
                console.log(status)
            }
            fetchRequestStatus()
            
        }
    }, [selectedChat])

    useEffect(() => {
        if (chatHistory.length > 0) {
            const sortedHistory = [...chatHistory].sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
            setMessages(sortedHistory); // Updating messages with sorted history
            console.log("THIS IS IT", sortedHistory)
        }
    }, [chatHistory]);

    useEffect(() => {
         scrollToBottom()
     }, [messages])

    function sendMessage() {
        if(!selectedChat) return

        if(text.length === 0) return

        const newMessage = {
            sentBySelf: true,
            message: text
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

    const getChatHistory = async (id) => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getChatHistory", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token"), groupchat_id: id})
            });

            if(!response.ok) {
                throw new Error("Failed to fetch chat history");
            }

            const result = await response.json();
            return result

        } catch(error) {
            console.error("Error fetching chat history: ", error)
        }
    }

    // Check Roommate Request Status
    const getRoommateRequestStatus = async () => {
        // check if there is an active request in this chat and who sent it
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getRoommateRequestStatus", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token"), groupchat_id: selectedChat[2]})
            });

            if(!response.ok) {
                throw new Error("Failed to fetch roommate request status");
            }

            const result = await response.json();
            return result

        } catch(error) {
            console.error("Error fetching roommate request status: ", error)
        }
    }


    // Request Roommate
    const requestRoommate = async () => {

        console.log("Requesting Roommate...")


        try {
            const roommateRequest = JSON.stringify({
                token: localStorage.getItem("token"),
                groupchat_id: selectedChat[2],
                response: 1
            })

            const response = await fetch("https://roomie.ddns.net:8080/chat/requestRoommate", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: roommateRequest,
            });

            if (!response.ok) {
                throw new Error("Roommate request failed. Please try again.");
            }

        } catch (error) {
            console.error("Error calling roommate request", error)
        }
    }



    return (
        <div className="holdChat">
            <div className="messageArea" ref={messageAreaRef}>
                <h5 className="chatNote" >You are chatting with {name}</h5>

                {messages.map((msg, index) => (
                    msg.sentBySelf === true ? (
                        <p key={index} className="right bubble"> {msg.message}</p>
                    ) : (
                        <p key={index} className="left bubble"> {msg.message}</p>
                    )
                ))}

            </div>
            <div className="messageInput">


                <button onClick={requestRoommate} className="chatButton">
                    <i className="bi bi-hand-thumbs-up"/>
                </button>

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
                <button onClick={sendMessage} className="chatButton">
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