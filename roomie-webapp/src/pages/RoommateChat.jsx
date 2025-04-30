import RoommateNavBar from "../components/RoommateNavBar";
import "./RoommateChat.css";
import { useEffect, useState, useCallback, useRef } from "react";

function RoommateChat() {
    const [groupchat, setGroupchat] = useState();
    const [chatHistory, setChatHistory] = useState([]);
    const [groupchatMembers, setGroupchatMembers] = useState([]);
    const [emailToNameMap, setEmailToNameMap] = useState({});
    const [text, setText] = useState("");

    const bottomRef = useRef(null);
    const prevMessageCountRef = useRef(0);

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

    const getChatHistory = async (id) => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getChatHistory", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token"), groupchat_id: id }),
            });

            if (!response.ok) {
                throw new Error("Failed to fetch chat history");
            }

            const result = await response.json();
            return result;
        } catch (error) {
            console.error("Error fetching chat history: ", error);
        }
    };

    const getAllUserInformation = useCallback(async () => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getAllUserInformation", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    groupchat_id: groupchat,
                    token: localStorage.getItem("token"),
                }),
            });

            if (!response.ok) throw new Error("Failed to fetch all user information");

            const result = await response.json();
            setGroupchatMembers(result);

            const emailMap = {};
            result.forEach((member) => {
                emailMap[member.email] = {
                    firstName: member.firstName,
                    lastName: member.lastName,
                    profilePicture: member.profilePicture,
                };
            });

            setEmailToNameMap(emailMap);
        } catch (error) {
            console.error("Error fetching all user information", error);
        }
    }, [groupchat]);

    useEffect(() => {
        if (groupchat) {
            getAllUserInformation();
        }
    }, [groupchat, getAllUserInformation]);

    useEffect(() => {
        let interval;

        if (groupchat) {
            const fetchChatHistory = async () => {
                const history = await getChatHistory(groupchat);
                if (history) setChatHistory(history);
            };

            fetchChatHistory(); // initial fetch
            interval = setInterval(fetchChatHistory, 3000); // poll every 3 sec
        }

        return () => {
            if (interval) clearInterval(interval);
        };
    }, [groupchat]);

    const scrollToBottom = () => {
        requestAnimationFrame(() => {
            bottomRef.current?.scrollIntoView({ behavior: "smooth" });
        });
    };

    useEffect(() => {
        if (chatHistory.length > prevMessageCountRef.current) {
            scrollToBottom();
        }
        prevMessageCountRef.current = chatHistory.length;
    }, [chatHistory]);

    function sendMessage() {
        if (text.length === 0) return;

        const newMessage = {
            sentBySelf: true,
            message: text
        }

        setChatHistory((prevMessages) => [...prevMessages, newMessage]);

        const sendMessageData = async () => {
            try {
                const messageData = JSON.stringify({
                    token: localStorage.getItem("token"),
                    groupchat_id: groupchat,
                    message: encodeURIComponent(text),
                });

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
                console.error("Error sending message", error);
            }
        };

        sendMessageData();
        setText("");
    }

    function handleKeyPress(e) {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage(e);
            e.target.style.height = "50px";
        }
    }

    return (
        <>
            <RoommateNavBar />

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
                            />
                        ))}
                    </div>
                    <div className="chat-area">
                        {chatHistory.map((msg, index) => {
                            const isLastMessageBySender =
                                index === chatHistory.length - 1 || chatHistory[index + 1].senderEmail !== msg.senderEmail;
                            const isFirstMessageBySender =
                                index === 0 || chatHistory[index - 1].senderEmail !== msg.senderEmail;

                            // Determine if it's a stacked middle message (not last)
                            const isStacked = !isLastMessageBySender;

                            return (
                                <div key={index} className={`hold-message ${msg.sentBySelf ? "right" : ""}`}>
                                    {isFirstMessageBySender && emailToNameMap[msg.senderEmail] && (
                                        <label>
                                            {emailToNameMap[msg.senderEmail]?.firstName}{" "}
                                            {emailToNameMap[msg.senderEmail]?.lastName}
                                        </label>
                                    )}
                                    <div
                                        className={`${
                                            isLastMessageBySender ? "bubble" : "bubble-rounded"
                                        } ${msg.sentBySelf ? "right" : "left"} ${
                                            isStacked ? (msg.sentBySelf ? "stacked-right" : "stacked-left") : ""
                                        }`}
                                    >
                                        <div className="message-text">{decodeURIComponent(msg.message)}</div>
                                    </div>
                                </div>
                            );
                        })}
                        <div ref={bottomRef} />
                    </div>
                    <div className="message-input">
                        <textarea
                            value={text}
                            onChange={(e) => setText(e.target.value)}
                            onKeyDown={handleKeyPress}
                            onInput={(e) => {
                                e.target.style.height = "50px";
                                e.target.style.height = `${e.target.scrollHeight}px`;
                            }}
                            style={{ resize: "none", overflowY: "hidden" }}
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
    );
}

export default RoommateChat;
