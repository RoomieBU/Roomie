import RoommateNavBar from "../components/RoommateNavBar";
import "./RoommateChat.css"
import { useEffect, useState, useCallback, useRef } from "react";

function RoommateChat() {

    const [groupchat, setGroupchat] = useState()
    const [chatHistory, setChatHistory] = useState([])
    const [groupchatMembers, setGroupchatMembers] = useState([])
    const [emailToNameMap, setEmailToNameMap] = useState({});
    const [text, setText] = useState("")

    const bottomRef = useRef(null)

    // get groupchat id
    const checkIfConfirmed = async () => {
        const response = await fetch("https://roomie.ddns.net:8080/matches/isUserCurrentRoommate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ token: localStorage.getItem("token") }),
        });

        if (response.ok) {
            const data = await response.json();
            setGroupchat(data.groupchatid);
        }
    };

    useEffect(() => {
        checkIfConfirmed();
    }, []);

    // get chatHistory
    const getChatHistory = async (id) => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getChatHistory", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token"), groupchat_id: id })
            });

            if (!response.ok) {
                throw new Error("Failed to fetch chat history");
            }

            const result = await response.json();
            return result

        } catch (error) {
            console.error("Error fetching chat history: ", error)
        }
    }

    // get groupchat user information --> all users in the groupchatID
    const getAllUserInformation = useCallback(async () => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getAllUserInformation", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    groupchat_id: groupchat,
                    token: localStorage.getItem("token")
                })
            });
    
            if (!response.ok) throw new Error("Failed to fetch all user information");
    
            const result = await response.json(); // ✅ this parses the actual data
            console.log("User info:", result);
    
            setGroupchatMembers(result); // ✅ now setting actual user data

            const emailMap = {};
            result.forEach(member => {
                emailMap[member.email] = {
                    firstName: member.firstName,
                    lastName: member.lastName,
                    profilePicture: member.profilePicture,
                };
            });

            console.log("emailMap: ", emailMap)

            setEmailToNameMap(emailMap); // ✅ store mapping

        } catch (error) {
            console.error("Error fetching all user information", error);
            return null;
        }
    }, [groupchat]);


    useEffect(() => {
        if (groupchat) {
            const fetchChatHistory = async () => {
                const history = await getChatHistory(groupchat)
                console.log(history)
                setChatHistory(history)
            }
            fetchChatHistory()
            getAllUserInformation()
        }
    }, [groupchat, getAllUserInformation])


    const scrollToBottom = () => {
        requestAnimationFrame(() => {
            bottomRef.current?.scrollIntoView({ behavior: "smooth" });
        });
    };
    
    useEffect(() => {
        scrollToBottom();
    }, [chatHistory]);


    // send message to db
    function sendMessage() {

        if (text.length === 0) return


        const newMessage = {
            groupChatId: 3,
            id: 0,
            message: text,
            senderEmail: "slamarca@gmail.com",
            sentBySelf: true,
            timestamp: new Date()
        }

        setChatHistory((prevMessages) => [...prevMessages, newMessage])

        const sendMessageData = async () => {
            try {
                const messageData = JSON.stringify({
                    token: localStorage.getItem("token"),
                    groupchat_id: groupchat,
                    message: encodeURIComponent(text)
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


    return (
        <>
            <RoommateNavBar/>

            <div className="chat-page-container">
                <div className="chat-wrapper">
                    <div className="chat-header">
                        {groupchatMembers.map((member, index) => (
                            <div
                            key={index}
                            className="roommate-picture"
                            style={{
                                backgroundImage: `url(${member.profilePicture})`,
                                backgroundSize: "cover",
                                backgroundPosition: "center",
                            }}
                            />))}
                    </div>
                    <div className="chat-area">
                        {chatHistory.map((msg, index) => (
                            <div
                                key={index}
                                className={`bubble ${msg.sentBySelf ? "right" : "left"}`}
                            >
                                {emailToNameMap[msg.senderEmail] && (
                                    <label>
                                        {emailToNameMap[msg.senderEmail]?.firstName} {emailToNameMap[msg.senderEmail]?.lastName}
                                    </label>
                                )}
                                <div className="message-text">{decodeURIComponent(msg.message)}</div>
                            </div>
                        ))}
                        <div ref={bottomRef} />
                    </div>
                    <div className="message-input">
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
                        <button className="chatButton" onClick={sendMessage}>
                            <i className="bi bi-send" />
                        </button>
                    </div>
                </div>
            </div>
        </>
    )
}

export default RoommateChat
