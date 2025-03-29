import "./Chat.css"
import { useState, useEffect, useRef } from "react"
import PropTypes from 'prop-types';

function Chat({ selectedChat }) {
    const [text, setText] = useState('')
    const [conversation, setConversation] = useState([])

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
        // send message to db and to matched roomate...
        if (text.trim() !== '') {
          
            const newMessage = {
                type: 'message',
                content: text,
                timestamp: new Date().getTime()
            }

            const newResponse = {
                type: 'response',
                content: "You are so right",
                timestamp: new Date().getTime() + 1
            }

            // Add both to conversation array --> will be different when chat implemented
            setConversation([...conversation, newMessage, newResponse])
            
            // Clear the input field after sending
            setText('');

            // Reset textarea height
            const textarea = document.querySelector(".messageTextBox")
            if(textarea)
                textarea.style.height = "50px"
          }

        
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
                {conversation.map((item, index) => (
                    item.type === 'message' ? (
                        <p key={index} className="right bubble">{item.content}</p>
                    ) : (
                        <p key={index} className="left bubble">{item.content}</p>
                    )
                ))}
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